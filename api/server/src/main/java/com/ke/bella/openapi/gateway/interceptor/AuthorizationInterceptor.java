package com.ke.bella.openapi.gateway.interceptor;

import static com.ke.bella.openapi.gateway.interceptor.ConcurrentStartInterceptor.ASYNC_REQUEST_MARKER;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.modules.apikey.ApikeyInfo;
import com.ke.bella.openapi.common.exception.OneTokenException;
import com.ke.bella.openapi.service.ApikeyService;

/**
 * Authorization interceptor for console and API endpoints.
 * Validates API keys, checks permissions, and handles alternative auth headers.
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final ApikeyService apikeyService;
    private final boolean userQuotaEditEnabled;

    public AuthorizationInterceptor(ApikeyService apikeyService,
                                    @Value("${bella.console.user-quota-edit-enabled:false}") boolean userQuotaEditEnabled) {
        this.apikeyService = apikeyService;
        this.userQuotaEditEnabled = userQuotaEditEnabled;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (Boolean.TRUE.equals(request.getAttribute(ASYNC_REQUEST_MARKER))
                || request.getDispatcherType() == DispatcherType.ASYNC) {
            return true;
        }

        // Check if there's an operator context (console/admin access via SessionAuthFilter)
        Operator op = OneTokenContext.getOperatorIgnoreNull();
        if (op != null) {
            String apikey = op.getManagerAk();
            ApikeyInfo apikeyInfo = apikeyService.verifyAuth(apikey);
            if (apikeyInfo == null) {
                throw new OneTokenException.AuthorizationException("API key does not exist");
            }
            op.getOptionalInfo().put("roles", apikeyInfo.getRolePath().getIncluded());
            op.getOptionalInfo().put("excludes", apikeyInfo.getRolePath().getExcluded());
            op.getOptionalInfo().put("roleCode", apikeyInfo.getRoleCode());
            op.getOptionalInfo().put("userQuotaEditEnabled", userQuotaEditEnabled);
            EndpointContext.setApikey(apikeyInfo);
            if (!apikeyInfo.hasPermission(request.getRequestURI())) {
                throw new OneTokenException.AuthorizationException("No operation permission");
            }
        } else {
            // API key authentication via Authorization header
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

            // Fallback to protocol-specific alternative headers
            if (StringUtils.isEmpty(auth)) {
                String alternativeHeader = getAlternativeHeader(request.getRequestURI());
                if (alternativeHeader != null) {
                    auth = request.getHeader(alternativeHeader);
                }
                if (StringUtils.isEmpty(auth)) {
                    throw new OneTokenException.AuthorizationException("Authorization is empty");
                }
            }

            ApikeyInfo apikeyInfo = apikeyService.verifyAuth(auth);
            boolean hasPermission = apikeyInfo.hasPermission(request.getRequestURI());

            if (apikeyInfo.hasAllocatedPermission()) {
                String userAkCode = request.getHeader(OneTokenContext.BELLA_USER_AK_HEADER);
                if (StringUtils.isNotEmpty(userAkCode)) {
                    ApikeyInfo userAkInfo = apikeyService.queryByCode(userAkCode, true);
                    userAkInfo.setApikey(auth);
                    apikeyInfo = userAkInfo;
                }
            }

            EndpointContext.setApikey(apikeyInfo);

            if (!hasPermission) {
                throw new OneTokenException.AuthorizationException("No operation permission");
            }

            // Set operator from ucid header if present
            String user = request.getHeader("ucid");
            if (user != null) {
                OneTokenContext.setOperator(Operator.builder().userId(0L).sourceId(user).build());
            }
        }

        return true;
    }

    /**
     * Get protocol-specific alternative authentication header name based on request URI.
     *
     * @param uri the request URI
     * @return alternative header name, or null if none
     */
    private String getAlternativeHeader(String uri) {
        // Gemini API uses x-goog-api-key as authentication header
        if (uri.startsWith("/v1beta/models") || uri.startsWith("/v1beta1/publishers/google/models")) {
            return "x-goog-api-key";
        }
        return null;
    }
}
