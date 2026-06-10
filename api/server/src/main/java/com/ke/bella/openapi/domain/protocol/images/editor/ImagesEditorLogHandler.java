package com.ke.bella.openapi.domain.protocol.images.editor;

import com.ke.bella.openapi.domain.protocol.images.ImagesLogHandler;
import org.springframework.stereotype.Component;

/**
 * 图片编辑接口的日志处理器
 */
@Component
public class ImagesEditorLogHandler extends ImagesLogHandler {

    @Override
    public String endpoint() {
        return "/v1/images/edits";
    }
}
