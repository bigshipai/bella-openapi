package com.ke.bella.openapi.event;

import com.ke.bella.openapi.apikey.ApikeyInfo;
import com.ke.bella.openapi.apikey.ApikeyOps;
import com.ke.bella.openapi.db.repo.ApikeyRepo;
import com.ke.bella.openapi.event.ApiKeyTransferEvent;
import com.ke.bella.openapi.service.ApikeyService;
import com.ke.bella.openapi.generated.tables.pojos.ApikeyDB;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * API Key transfer event listener
 * Responsible for handling post-transfer cache cleanup and other follow-up operations
 *
 * @author claude
 */
@Slf4j
@Component
public class TransferApikeyListener {

    @Autowired
    private ApikeyRepo apikeyRepo;

    @Autowired
    private ApikeyService apikeyService;

    /**
     * Handle API Key transfer event
     * Execute cache cleanup asynchronously to avoid affecting main business logic
     *
     * @param event API Key transfer event
     */
    @Async("cacheTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleApiKeyTransfer(ApiKeyTransferEvent event) {
        try {
            log.info("Started processing API Key transfer event - akCode: {}, from: {} -> to: {}, operator: {}({})",
                    event.getAkCode(),
                    event.getFromOwnerName(),
                    event.getToOwnerName(),
                    event.getOperatorName(),
                    event.getOperatorUid());

            clearTransferredApikeyCaches(event.getAkCode());

            log.info("API Key transfer event completed - akCode: {}", event.getAkCode());
        } catch (Exception e) {
            log.warn("API Key transfer cache cleanup failed, may have stale data - akCode: {}, error: {}",
                    event.getAkCode(), e.getMessage(), e);
        }
    }

    /**
     * Clear transferred API Key related caches
     * Including main API Key and all sub API Key caches
     *
     * @param akCode API Key code
     */
    private void clearTransferredApikeyCaches(String akCode) {
        // Clear main API Key cache
        ApikeyInfo mainApikey = apikeyRepo.queryByCode(akCode);
        if(mainApikey != null) {
            apikeyService.clearApikeyCache(mainApikey.getAkSha());
            log.debug("Cleared main API Key cache: akCode={}, akSha={}", akCode, mainApikey.getAkSha());
        }

        // Clear all sub API Key caches
        ApikeyOps.ApikeyCondition condition = new ApikeyOps.ApikeyCondition();
        condition.setParentCode(akCode);
        List<ApikeyDB> subApikeys = apikeyRepo.listAccessKeys(condition);

        if(CollectionUtils.isNotEmpty(subApikeys)) {
            for (ApikeyDB subApikey : subApikeys) {
                apikeyService.clearApikeyCache(subApikey.getAkSha());
                log.debug("Cleared sub API Key cache: akCode={}, akSha={}", subApikey.getCode(), subApikey.getAkSha());
            }
            log.info("API Key transfer complete, cleared {} sub API Key caches", subApikeys.size());
        } else {
            log.debug("API Key transfer complete, no sub API Key caches to clear");
        }
    }
}
