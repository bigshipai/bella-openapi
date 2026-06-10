//package com.ke.bella.openapi.config.interceptor;
//
//import com.ke.bella.openapi.common.constant.EntityConstants;
//import com.ke.bella.openapi.common.context.EndpointContext;
//import com.ke.bella.openapi.common.context.OneTokenContext;
//import com.ke.bella.openapi.common.model.Operator;
//import com.ke.bella.openapi.controller.apikey.dto.ApikeyInfo;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
/// **
// * TODO 感觉没有任何的用处,暂时注释掉
// */
//@Component
//public class TestEnvironmentInterceptor implements HandlerInterceptor {
//
//	@Value("${spring.profiles.active}")
//	private String profile;
//
//	@Override
//	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
//							 @NotNull Object handler) throws Exception {
//		if ("test".equals(profile)) {
//			ApikeyInfo apikey = EndpointContext.getApikey();
//			Operator op = OneTokenContext.getOperatorIgnoreNull();
//			return apikey.getOwnerType().equals(EntityConstants.SYSTEM)
//				|| (op != null && StringUtils.isNotBlank(op.getManagerAk()));
//		}
//		return true;
//	}
//}
