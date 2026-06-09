package com.ke.bella.openapi.modules.safety;

public interface ISafetyResultStorage {

    void addRiskData(Object riskData, boolean isRequest);

    Object getRequestRiskData();

    Object getResponseRiskData();
}
