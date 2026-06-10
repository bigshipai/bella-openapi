package com.ke.bella.openapi.utils;

import com.ke.bella.openapi.common.context.OneTokenContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;

@Slf4j
public class SseHelper {

	public static SseEmitter createSse(long timeout, String reqId) {
		final String traceId = OneTokenContext.getTraceId();
		SseEmitter sse = new SseEmitter(timeout);

		sse.onCompletion(() -> log.info("[traceId={}][{}] Connection ended...................", traceId, reqId));
		sse.onTimeout(() -> log.info("[traceId={}][{}] Connection timeout...................", traceId, reqId));
		sse.onError(e -> log.info("[traceId={}][{}] Connection error,{}", traceId, reqId, e.toString()));
		log.info("[traceId={}][{}] SSE connection created successfully!", traceId, reqId);

		return sse;
	}

	public static void send(SseEmitter sse, SseEventBuilder event) {
		try {
			sse.send(event);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	public static void sendEvent(SseEmitter sse, String event, Object data) {
		send(sse, SseEmitter.event().name(event).data(data));
	}

	public static void sendEvent(SseEmitter sse, Object data) {
		send(sse, SseEmitter.event().data(data));
	}

}
