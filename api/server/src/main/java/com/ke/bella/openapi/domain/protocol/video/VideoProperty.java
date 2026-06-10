package com.ke.bella.openapi.domain.protocol.video;

import com.ke.bella.openapi.domain.protocol.IProtocolProperty;

public interface VideoProperty extends IProtocolProperty {

    default Integer getRpm() {
        return null;
    }
}
