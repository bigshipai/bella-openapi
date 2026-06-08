package com.ke.bella.openapi.gateway.interceptor;

import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.apikey.ApikeyInfo;
import com.ke.bella.openapi.common.exception.OneTokenException;
import com.ke.bella.openapi.service.ApikeyService;
import com.ke.bella.openapi.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

import static com.ke.bella.openapi.gateway.interceptor.ConcurrentStartInterceptor.ASYNC_REQUEST_MARKER;

@Component
public class MonthQuotaInterceptor implements HandlerInterceptor {
    @Autowired
    private ApikeyService apikeyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(Boolean.TRUE.equals(request.getAttribute(ASYNC_REQUEST_MARKER))
                || request.getDispatcherType() == DispatcherType.ASYNC) {
            return true;
        }
        ApikeyInfo apikey = EndpointContext.getApikey();
        // 非子ak 或 已指定额度的子ak
        if(apikey.getParentInfo() == null || apikey.getMonthQuota().doubleValue() > 0) {
            BigDecimal cost = apikeyService.loadCost(apikey.getCode(), DateTimeUtils.getCurrentMonth());
            double costVal = cost.doubleValue() / 100.0;
            if(apikey.getMonthQuota().doubleValue() <= costVal) {
                String msg = "Monthly quota limit reached, limit:" + apikey.getMonthQuota() + ", cost:" + costVal;
                throw new OneTokenException.RateLimitException(msg);
            }
        }
        // 父ak的总额度不能超出
        if(apikey.getParentInfo() != null) {
            BigDecimal quota = apikey.getParentInfo().getMonthQuota();
            BigDecimal cost = apikeyService.loadCost(apikey.getParentCode(), DateTimeUtils.getCurrentMonth());
            double costVal = cost.doubleValue() / 100.0;
            if(quota.doubleValue() <= costVal) {
                String msg = "Main AK total quota limit reached, limit:" + quota + ", cost:" + costVal;
                throw new OneTokenException.RateLimitException(msg);
            }
        }
        return true;
    }
}
