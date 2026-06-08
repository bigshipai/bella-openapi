package com.ke.bella.openapi.space;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * function:
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberOp extends Operator {

    @NotEmpty(message = "spaceCode cannot be empty")
    @Size(max = 64, message = "spaceCode cannot exceed 64 characters")
    private String spaceCode;

    /**
     * Role code
     */
    @NotEmpty(message = "roleCode cannot be empty")
    @Size(max = 64, message = "roleCode cannot exceed 64 characters")
    private String roleCode;

    /**
     * Member list
     */
    private List<Member> members;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Member {

        /**
         * Member UID
         */
        private String memberUid;

        /**
         * Member name
         */
        private String memberName;
    }

}
