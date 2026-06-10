package com.ke.bella.openapi.domain.protocol.images.generator;

import com.ke.bella.openapi.domain.protocol.IProtocolAdaptor;
import com.ke.bella.openapi.domain.protocol.images.ImagesProperty;
import com.ke.bella.openapi.domain.protocol.images.ImagesRequest;
import com.ke.bella.openapi.domain.protocol.images.ImagesResponse;

/**
 * 文生图适配器接口
 */
public interface ImagesGeneratorAdaptor<T extends ImagesProperty> extends IProtocolAdaptor {

    /**
     * 生成图片
     *
     * @param request  请求参数
     * @param url      请求地址
     * @param property 属性配置
     *
     * @return 响应结果
     */
    ImagesResponse generateImages(ImagesRequest request, String url, T property);
}
