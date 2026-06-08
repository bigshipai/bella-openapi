package com.ke.bella.openapi.safety;

import com.ke.bella.openapi.TaskExecutor;
import com.ke.bella.openapi.common.exception.OneTokenException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Safety check delegator implementation
 * Execute different safety check strategies based on different modes through adapter pattern
 *
 * @param <T> Safety check request type
 */
@Slf4j
@AllArgsConstructor
public class SafetyCheckDelegator<T extends SafetyCheckRequest> implements ISafetyCheckService<T>, ISafetyResultStorageDelegator {

    /**
     * Actual safety check service (singleton)
     */
    private final ISafetyCheckService<T> delegate;

    /**
     * Safety check mode context
     */
    private final SafetyCheckMode mode;

    /**
     * Result storage service
     */
    private final ISafetyResultStorage storage;

    @Override
    public Object safetyCheck(T request, boolean isMock) {
        if(delegate == null) {
            return null;
        }
        switch (mode) {
        case sync:
            return executeSyncCheck(request, isMock);
        case skip:
            return null;
        default:
            executeAsyncCheck(request, isMock);
            return null;
        }
    }

    private Object executeSyncCheck(T request, boolean isMock) {
        try {
            Object result = delegate.safetyCheck(request, isMock);
            if(result != null) {
                addRiskData(result, request.isRequest());
            }
            return result;
        } catch (OneTokenException.SafetyCheckException e) {
            log.warn("Async safety check found sensitive data: requestId={}, sensitiveData={}",
                    request.getRequestId(), e.getSensitive());
            if(e.getSensitive() != null) {
                addRiskData(e.getSensitive(), request.isRequest());
            }
            return e.getSensitive();
        } catch (Exception e) {
            log.warn("Async safety check error: requestId={}, error={}",
                    request.getRequestId(), e.getMessage(), e);
            return null;
        }
    }

    private void executeAsyncCheck(T request, boolean isMock) {
        TaskExecutor.submit(() -> executeSyncCheck(request, isMock));
    }

    @Override
    public ISafetyResultStorage getStorage() {
        return storage;
    }
}
