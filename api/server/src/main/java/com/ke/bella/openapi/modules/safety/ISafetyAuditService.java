package com.ke.bella.openapi.modules.safety;

public interface ISafetyAuditService {
    Byte fetchLevelByCertifyCode(String certifyCode);
}
