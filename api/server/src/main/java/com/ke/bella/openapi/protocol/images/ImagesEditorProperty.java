package com.ke.bella.openapi.protocol.images;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 图片编辑接口的属性配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImagesEditorProperty extends ImagesProperty {

    /**
     * 是否支持文件上传
     */
    private boolean supportFile = true;

    /**
     * 是否支持URL输入
     * 默认为false，只支持文件上传
     */
    private boolean supportUrl = false;

    /**
     * 是否支持Base64输入
     * 默认为false，只支持文件上传
     */
    private boolean supportBase64 = false;

    @Override
    public Map<String, String> description() {
        Map<String, String> desc = super.description();
        desc.put("supportFile", "Support file upload (default: true)");
        desc.put("supportUrl", "Support URL input (default: false)");
        desc.put("supportBase64", "Support Base64 input (default: false)");
        return desc;
    }
}
