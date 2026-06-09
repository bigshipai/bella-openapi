package com.ke.bella.openapi.controller.login;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.controller.login.session.SessionManager;

import static com.ke.bella.openapi.controller.login.config.OneTokenLoginConfiguration.redirectParameter;

/**
 * Session 认证过滤器，仅负责从请求中提取 session token 并设置 Operator 上下文。
 * 不处理任何业务端点（登录/注册/登出等逻辑已迁移至 AuthController）。
 */
public class SessionAuthFilter extends OncePerRequestFilter {

    private final LoginProperties properties;
    private final SessionManager sessionManager;
    public static final String REDIRECT_HEADER = "X-Redirect-Login";
    public static final String CONSOLE_HEADER = "X-BELLA-CONSOLE";

    /**
     * 无需认证即可访问的公开路径前缀。
     * 这些路径即使携带 X-BELLA-CONSOLE header 也不触发 401 重定向。
     */
    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/openapi/oauth",      // OAuth 配置查询和回调
            "/openapi/login",      // 登录
            "/openapi/register",   // 注册
            "/openapi/logout",     // 登出
            "/openapi/userInfo"    // 用户信息探测（由 AuthController 自行处理 401）
    );

    public SessionAuthFilter(LoginProperties properties, SessionManager sessionManager) {
        this.properties = properties;
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // 如果配置了 authorizationHeader，携带该 header 的请求直接放行（由 Interceptor 处理认证）
            if (StringUtils.isNotBlank(properties.getAuthorizationHeader())) {
                String auth = request.getHeader(properties.getAuthorizationHeader());
                if (StringUtils.isNotBlank(auth)) {
                    chain.doFilter(request, response);
                    return;
                }
            }

            // 尝试从 session 获取已登录用户
            Operator operator = sessionManager.getSession(request);
            if (operator != null) {
                OneTokenContext.setOperator(operator);
                chain.doFilter(request, response);
                return;
            }

            // 公开路径（OAuth 配置、登录、注册等）直接放行，不触发 401 重定向
            if (isPublicPath(request.getRequestURI())) {
                chain.doFilter(request, response);
                return;
            }

            // 控制台请求且已配置登录页 → 返回 401 并设置重定向 header
            if ("true".equals(request.getHeader(CONSOLE_HEADER))
                    && StringUtils.isNotBlank(properties.getLoginPageUrl())) {
                response.setHeader(REDIRECT_HEADER,
                        properties.getLoginPageUrl() + "?" + redirectParameter + "=");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 未认证但非控制台请求，继续放行（公开端点或由 Interceptor 处理）
            chain.doFilter(request, response);
        } finally {
            OneTokenContext.clearAll();
            sessionManager.renew(request);
        }
    }

    /**
     * 判断请求路径是否为公开路径（无需认证即可访问）。
     */
    private boolean isPublicPath(String uri) {
        for (String prefix : PUBLIC_PATH_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
