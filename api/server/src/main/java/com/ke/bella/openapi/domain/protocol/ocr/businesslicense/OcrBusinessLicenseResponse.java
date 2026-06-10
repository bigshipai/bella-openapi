package com.ke.bella.openapi.domain.protocol.ocr.businesslicense;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ke.bella.openapi.domain.protocol.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * OCR business license recognition response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class OcrBusinessLicenseResponse extends ApiResponse {
    private static final long serialVersionUID = 1L;

    private String request_id;                  // Request unique identifier
    private BusinessLicenseData data;           // Recognition result data

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BusinessLicenseData implements Serializable {
        private static final long serialVersionUID = 1L;

        private String unified_social_credit_code;  // Unified social credit code
        private String license_number;              // License number
        private String name;                        // Name
        private String entity_type;                 // Entity type
        private String legal_representative;        // Legal representative
        private String business_scope;              // Business scope
        private String registered_capital;          // Registered capital
        private String paid_in_capital;             // Paid-in capital (optional)
        private String establishment_date;          // Establishment date (format: yyyy/MM/dd)
        private String business_term_start;         // Business term start (format: yyyy/MM/dd)
        private String business_term_end;           // Business term end (format: yyyy/MM/dd, "permanent" for lifetime)
        private String address;                     // Address
        private String issue_date;                  // Issue date (format: yyyy/MM/dd)
        private String issue_authority;             // Issue authority
        private String taxpayer_id;                 // Taxpayer ID
        private String composition_form;            // Composition form

    }
}
