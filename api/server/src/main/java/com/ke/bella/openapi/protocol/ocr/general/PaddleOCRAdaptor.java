package com.ke.bella.openapi.protocol.ocr.general;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ke.bella.openapi.protocol.ocr.BaiduOcrProperty;
import com.ke.bella.openapi.protocol.ocr.ImageRetrievalService;
import com.ke.bella.openapi.protocol.ocr.OcrRequest;
import com.ke.bella.openapi.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Baidu AI Cloud Qianfan PaddleOCR-VL Adapter
 */
@Slf4j
@Component("paddleOCR")
public class PaddleOCRAdaptor implements GeneralAdaptor<BaiduOcrProperty> {

    @Autowired
    private ImageRetrievalService imageRetrievalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String getDescription() {
        return "Baidu Qianfan PaddleOCR-VL Protocol";
    }

    @Override
    public Class<BaiduOcrProperty> getPropertyClass() {
        return BaiduOcrProperty.class;
    }

    @Override
    public OcrGeneralResponse general(OcrRequest request, String url, BaiduOcrProperty property) {
        // 1. Build PaddleOCR request
        PaddleOCRRequest paddleRequest = buildPaddleRequest(request);

        // 2. Build HTTP request (JSON format)
        Request httpRequest = authorizationRequestBuilder(property.getAuth())
                .url(url)
                .post(buildJsonBody(paddleRequest))
                .build();

        // 3. Clear large data
        clearLargeData(request, paddleRequest);

        // 4. Send request
        PaddleOCRResponse paddleResponse = HttpUtils.httpRequest(httpRequest, PaddleOCRResponse.class);

        // 5. Convert response
        return responseConvert(paddleResponse);
    }

    /**
     * Build PaddleOCR request
     * Only responsible for file field retrieval and conversion, all parameters are passed through to the channel
     */
    private PaddleOCRRequest buildPaddleRequest(OcrRequest request) {
        Map<String, Object> extraBody = request.getExtra_body();

        // Handle file input
        if (StringUtils.hasText(request.getFileId())) {
            // Method 1: file_id -> convert to Base64, overwrite the file field in extraBody
            byte[] imageData = imageRetrievalService.getImageFromFileId(request.getFileId());
            String base64Data = Base64.getEncoder().encodeToString(imageData);

            // Create new Map to avoid modifying original extraBody
            Map<String, Object> params = extraBody != null ? new java.util.HashMap<>(extraBody) : new java.util.HashMap<>();
            params.put("file", base64Data);
            extraBody = params;

        } else if (extraBody == null || !extraBody.containsKey("file")) {
            // No file input provided
            throw new IllegalArgumentException(
                "Must provide either file_id or file (in the request body). " +
                "file can be a URL or Base64 encoded string"
            );
        }

        // All fields in extra_body (including file) are directly flattened and overridden to PaddleOCRRequest
        return PaddleOCRRequest.builder()
                .model(request.getModel())
                .extraParams(extraBody)
                .build();
    }

    /**
     * Build JSON request body
     */
    private RequestBody buildJsonBody(PaddleOCRRequest request) {
        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(request);
            return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBytes);
        } catch (Exception e) {
            log.error("Failed to serialize PaddleOCRRequest to JSON", e);
            throw new RuntimeException("Failed to build request body", e);
        }
    }

    /**
     * Response conversion: save the complete response object
     */
    private OcrGeneralResponse responseConvert(PaddleOCRResponse response) {
        // Check PaddleOCR error format (error object)
        if (response.getError() != null) {
            return buildErrorResponse(response);
        }

        // Directly save the complete result object, no conversion that loses information
        return OcrGeneralResponse.builder()
                .requestId(response.getId())
                .data(response.getResult())
                .build();
    }

    /**
     * Build error response
     * PaddleOCR-VL uses nested error objects, different from Baidu OCR's errorCode/errorMsg
     */
    private OcrGeneralResponse buildErrorResponse(PaddleOCRResponse response) {
        // Return the complete error object as data
        return OcrGeneralResponse.builder()
                .requestId(response.getId())
                .data(response.getError())
                .build();
    }
}
