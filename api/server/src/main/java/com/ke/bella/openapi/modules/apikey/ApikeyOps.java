package com.ke.bella.openapi.modules.apikey;

import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.common.model.PageCondition;
import com.ke.bella.openapi.common.model.PermissionCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public class ApikeyOps {
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApplyOp extends Operator {
        private String name;
        private String ownerType;
        private Long ownerUserId;  // Look up user by userId, backend auto-calculates correct ownerCode (used for person type)
        private String ownerCode;
        private String ownerName;
        private String roleCode;
        private BigDecimal monthQuota;
        private String remark;
        private String managerCode;
        private String managerName;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ManagerOp extends Operator {
        private String code;
        private Long managerUserId;  // Look up user by userId, backend auto-calculates correct managerCode
        private String managerCode;
        private String managerName;
        private String reason;
        private Boolean syncChildren;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeOwnerOp extends Operator {
        @NotBlank(message = "code cannot be empty")
        private String code;
        @NotBlank(message = "targetOwnerType cannot be empty")
        private String targetOwnerType;
        private String targetOwnerCode;
        private String targetOwnerName;
        private String reason;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeParentOp extends Operator {
        @NotBlank(message = "code cannot be empty")
        private String code;
        @NotBlank(message = "targetParentCode cannot be empty")
        private String targetParentCode;
        private String reason;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeResult {
        private String code;
        private String action;
        private Integer affectedCount;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OwnerInheritanceOp extends Operator {
        @NotBlank(message = "parentCode cannot be empty")
        private String parentCode;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OwnerInheritancePreview {
        private String parentCode;
        private String parentOwnerType;
        private String parentOwnerCode;
        private String parentOwnerName;
        private Integer mismatchedCount;
        private List<OwnerInheritanceItem> items;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OwnerInheritanceItem {
        private String code;
        private String akDisplay;
        private String name;
        private String currentOwnerType;
        private String currentOwnerCode;
        private String currentOwnerName;
        private String targetOwnerType;
        private String targetOwnerCode;
        private String targetOwnerName;
        private String managerCode;
        private String managerName;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NameOp extends Operator {
        private String code;
        private String name;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ServiceOp extends Operator {
        private String code;
        private String serviceId;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleOp extends Operator {
        private String code;
        private String roleCode;
        private List<String> paths;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CertifyOp extends Operator {
        private String code;
        private String certifyCode;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuotaOp extends Operator {
        private String code;
        private BigDecimal monthQuota;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QpsLimitOp extends Operator {
        private String code;
        /**
         * QPS limit value
         * null or 0: use system default
         * positive number: specific QPS limit
         * negative number: unlimited
         */
        private Integer qpsLimit;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CodeOp extends Operator {
        private String code;
    }

    @Data
    public static class ApikeyCondition extends PermissionCondition {
        private String ownerType;
        private String excludeOwnerType; // Exclude specified owner type, e.g. filter personal AK if "person"
        private String ownerCode;
        private String parentCode;
        private String name;
        private String serviceId;
        private String searchParam; // Fuzzy search for name / serviceId
        private String ownerSearch; // Fuzzy search for ownerName / ownerCode
        private String managerCode; // Exact match manager
        private String managerSearch; // Fuzzy search for managerName / managerCode
        private String outEntityCode;
        private boolean includeChild;
        private boolean onlyChild; // true: only return child AKs (parent_code != ''), used for manager perspective pagination of sub AKs
        private String status;
    }
}
