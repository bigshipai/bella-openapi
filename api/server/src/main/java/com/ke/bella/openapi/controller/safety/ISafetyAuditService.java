package com.ke.bella.openapi.controller.safety;

public interface ISafetyAuditService {
    Byte fetchLevelByCertifyCode(String certifyCode);
}
