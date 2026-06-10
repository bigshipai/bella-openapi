package com.ke.bella.openapi.job.worker;

import com.ke.bella.openapi.config.executor.TaskExecutor;
import com.ke.bella.openapi.config.client.OneTokenServerClient;
import com.ke.bella.openapi.domain.protocol.AdaptorManager;
import com.ke.bella.openapi.domain.protocol.limiter.LimiterManager;
import com.ke.bella.openapi.controller.safety.ISafetyCheckService;
import com.ke.bella.openapi.controller.safety.SafetyCheckRequest;
import com.ke.bella.openapi.common.script.LuaScriptExecutor;
import com.ke.bella.openapi.config.client.OpenAiServiceFactory;
import com.ke.bella.openapi.config.properties.OneTokenApiProperties;
import com.ke.bella.openapi.domain.channel.ChannelService;
import com.ke.bella.openapi.jooqgen.tables.pojos.ChannelDB;
import com.theokanning.openai.service.OpenAiService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@ConditionalOnProperty(name = "bella.openapi.as-worker.enabled", havingValue = "true")
public class WorkerManager {

    @Resource
    private ChannelService channelService;
    @Resource
    private AdaptorManager adaptorManager;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private OpenAiServiceFactory openAiServiceFactory;
    @Resource
    private OneTokenApiProperties oneTokenApiProperties;
    @Resource
    private OneTokenServerClient oneTokenServerClient;
    @Resource
    private LuaScriptExecutor luaScriptExecutor;
    @Resource
    private LimiterManager limiterManager;
    @Resource
    private ISafetyCheckService<SafetyCheckRequest.Chat> chatSafetyCheckService;

    @Value("${bella.openapi.as-worker.remaining-capacity-threshold:0.7}")
    @Getter
    private double remainingCapacityThreshold;

    @Value("${bella.openapi.as-worker.min-age-seconds:1}")
    @Getter
    private long minAgeSeconds;

    @Value("${bella.openapi.as-worker.max-concurrency:100}")
    @Getter
    private int maxConcurrency;

    private OpenAiService openAiService;

    private final Map<String, WorkerContext> runningWorkers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        openAiService = openAiServiceFactory.create(oneTokenApiProperties.getServiceAk());
        TaskExecutor.scheduleAtFixedRate(() -> {
            try {
                refreshWorkers();
            } catch (Exception e) {
                log.error("Failed to refresh workers", e);
            }
        }, 60 * 2);
    }

    private void refreshWorkers() {
        List<ChannelDB> channels = channelService.listAllWorkerChannels();
        Map<String, String> channelMap = channels.stream()
                .collect(Collectors.toMap(ChannelDB::getChannelCode, ChannelDB::getQueueName));

        synchronized(runningWorkers) {
            runningWorkers.keySet().removeIf(channelCode -> {
                WorkerContext context = runningWorkers.get(channelCode);
                String queueName = channelMap.get(channelCode);

                if(queueName == null || !context.isSameQueue(queueName)) {
                    context.stop();
                    return true;
                } else {
                    return false;
                }
            });

            for (ChannelDB channel : channels) {
                if(!runningWorkers.containsKey(channel.getChannelCode())) {
                    WorkerContext workerContext = WorkerContext.builder()
                            .channel(channel)
                            .redissonClient(redissonClient)
                            .openAiService(openAiService)
                            .oneTokenServerClient(oneTokenServerClient)
                            .adaptorManager(adaptorManager)
                            .luaScriptExecutor(luaScriptExecutor)
                            .limiterManager(limiterManager)
                            .workerManager(this)
                            .chatSafetyCheckService(chatSafetyCheckService)
                            .build();
                    workerContext.start();
                    runningWorkers.put(channel.getChannelCode(), workerContext);
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if(runningWorkers.isEmpty()) {
            return;
        }

        log.info("Stopping {} workers...", runningWorkers.size());
        runningWorkers.values().forEach(WorkerContext::stop);

        for (int i = 0; i < 30; i++) {
            if(runningWorkers.values().stream().allMatch(WorkerContext::isStopped)) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        runningWorkers.clear();
        log.info("WorkerManager destroyed");
    }

}
