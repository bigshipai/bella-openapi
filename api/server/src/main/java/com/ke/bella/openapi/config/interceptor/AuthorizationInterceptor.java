package com.ke.bella.openapi.config.interceptor;

import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.exception.OneTokenException;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.controller.apikey.dto.ApikeyInfo;
import com.ke.bella.openapi.domain.apikey.ApikeyService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.ke.bella.openapi.config.interceptor.ConcurrentStartInterceptor.ASYNC_REQUEST_MARKER;

/**
 * Authorization interceptor for console and API endpoints.
 * Validates API keys, checks permissions, and handles alternative auth headers.
 * → 只拦截 Spring Controller 接口
 * → 静态资源不拦（除非你配置）
 * preHandle()    → 进 Controller 前
 * postHandle()   → Controller 执行完
 * afterCompletion() → 整个请求结束
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
			// 在这里处理那些,不同认证参数的解析.处理中转的需求,也就是说我这个服务可以仅仅作为一个代理服务器
			if (StringUtils.isEmpty(auth)) {
				String alternativeHeader = getAlternativeHeader(request.getRequestURI());
				if (alternativeHeader != null) {
					auth = request.getHeader(alternativeHeader);
				}
				if (StringUtils.isEmpty(auth)) {
					throw new OneTokenException.AuthorizationException("Authorization is empty");
				}
			}
			//获取key信息,如果是代理中转的话,就不用了
			ApikeyInfo apikeyInfo = apikeyService.verifyAuth(auth);

			//校验key值支付有这个接口的访问权限
			boolean hasPermission = apikeyInfo.hasPermission(request.getRequestURI());
			if (!hasPermission) {
				throw new OneTokenException.AuthorizationException("No operation permission");
			}

			//TODO 这里也没有用,不用搞的那么复杂
			if (apikeyInfo.hasAllocatedPermission()) {
				String userAkCode = request.getHeader(OneTokenContext.BELLA_USER_AK_HEADER);
				if (StringUtils.isNotEmpty(userAkCode)) {
					ApikeyInfo userAkInfo = apikeyService.queryByCode(userAkCode, true);
					userAkInfo.setApikey(auth);
					apikeyInfo = userAkInfo;
				}
			}

			EndpointContext.setApikey(apikeyInfo);

			// Set operator from uc_id header if present//TODO 这里估计也是没有用的,后续去掉
			String user = request.getHeader("uc_id");
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
