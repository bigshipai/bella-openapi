package com.ke.bella.openapi.config.web;

import com.ke.bella.openapi.config.executor.TaskExecutor;
import com.ke.bella.openapi.config.properties.OneTokenApiProperties;
import com.ke.bella.openapi.domain.protocol.log.*;
import com.ke.bella.openapi.utils.IDGenerator;
import com.ke.bella.openapi.domain.log.LogRepo;
import com.ke.bella.openapi.domain.instance.InstanceRepo;
import com.ke.bella.openapi.job.queue.QueueClient;
import com.ke.bella.openapi.domain.protocol.AdaptorManager;
import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.cost.CostCounter;
import com.ke.bella.openapi.domain.protocol.limiter.LimiterManager;
import com.ke.bella.openapi.domain.protocol.metrics.MetricsManager;
import com.ke.bella.openapi.domain.endpoint.EndpointService;
import com.ke.bella.openapi.domain.apikey.ApikeyService;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

/**
 * @ EnableConfigurationProperties 的动作：它告诉 Spring，“
 * 请把 OpenapiProperties 这个类实例化，并把它当作一个 Bean 放入 Spring 容器中管理”。
 */
@EnableConfigurationProperties(OneTokenApiProperties.class)
@Configuration
public class OneTokenAutoConfig {

	private final InstanceRepo instanceRepo;
	private final MetricsManager metricsManager;
	private final LimiterManager limiterManager;
	private final SleepingWaitStrategy sleepingWaitStrategy = new SleepingWaitStrategy();
	private Disruptor<LogEvent> logDisruptor;
	private CostCounter costCounter;

	public OneTokenAutoConfig(InstanceRepo instanceRepo, MetricsManager metricsManager, LimiterManager limiterManager) {
		this.instanceRepo = instanceRepo;
		this.metricsManager = metricsManager;
		this.limiterManager = limiterManager;
	}

	/**
	 * 这里设置ID生成器的环境变量,这里的处理方法很巧妙,解决了多服务集群部署的问题
	 */
	@PostConstruct
	public void registerInstance() {
		Long id = instanceRepo.register(OneTokenContextHolder.getIp(),
			Objects.requireNonNull(OneTokenContextHolder.getPort()));
		IDGenerator.setInstanceId(id);
	}

	@Bean
	public AdaptorManager adaptorManager(List<IProtocolAdaptor> adaptors) {
		AdaptorManager manager = AdaptorManager.getInstance();
		adaptors.forEach(adaptor -> manager.register(adaptor.endpoint(), adaptor));
		return manager;
	}

	@Bean
	public CostCounter.CostRecorder costRecorder(ApikeyService service) {
		return service::recordCost;
	}

	@Bean
	public CostLogHandler.CostScripFetcher costScripFetcher(EndpointService service) {
		return service::fetchCostScript;
	}

	@Bean
	public CostCounter costCounter(CostCounter.CostRecorder costRecorder) {
		costCounter = new CostCounter(costRecorder);
		return costCounter;
	}

	@Bean
	public RingBuffer<LogEvent> logRingBuffer(List<LogRepo> logRepos, CostCounter costCounter, CostLogHandler.CostScripFetcher costScripFetcher) {
		Disruptor<LogEvent> disruptor = new Disruptor<>(LogEvent::new, 1024,
			DaemonThreadFactory.INSTANCE, ProducerType.MULTI, sleepingWaitStrategy);
		disruptor.handleEventsWith(new CostLogHandler(costCounter, costScripFetcher)).then(new LogRecordHandler(logRepos));
		disruptor.handleEventsWith(new MetricsLogHandler(metricsManager), new LimiterLogHandler(limiterManager));
		disruptor.setDefaultExceptionHandler(new LogExceptionHandler());
		disruptor.start();
		logDisruptor = disruptor;
		return disruptor.getRingBuffer();
	}

	@Bean
	public QueueClient queueClient(OneTokenApiProperties oneTokenApiProperties) {
		return QueueClient.getInstance(oneTokenApiProperties.getHost());
	}

	/**
	 * 优雅的关闭
	 */
	@PreDestroy
	public void gracefulShutdown() {
		TaskExecutor.shutdown();
		if (logDisruptor != null) {
			logDisruptor.shutdown();
		}
		if (costCounter != null) {
			costCounter.flush();
		}

		instanceRepo.unregister(OneTokenContextHolder.getIp(), OneTokenContextHolder.getPort());
	}
}
