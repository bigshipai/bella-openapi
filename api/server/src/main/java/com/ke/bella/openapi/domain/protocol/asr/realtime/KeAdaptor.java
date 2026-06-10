package com.ke.bella.openapi.domain.protocol.asr.realtime;

import com.ke.bella.openapi.domain.protocol.realtime.RealtimeProperty;
import org.springframework.stereotype.Component;

@Component("KeRealtimeAsr")
public class KeAdaptor extends com.ke.bella.openapi.domain.protocol.realtime.KeAdaptor implements RealTimeAsrAdaptor<RealtimeProperty> {
}
