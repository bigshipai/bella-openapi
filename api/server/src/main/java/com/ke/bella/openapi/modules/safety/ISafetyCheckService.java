package com.ke.bella.openapi.modules.safety;

public interface ISafetyCheckService<T extends SafetyCheckRequest> {
    Object safetyCheck(T request, boolean isMock);

    interface IChatSafetyCheckService extends ISafetyCheckService<SafetyCheckRequest.Chat> {
    }
}
