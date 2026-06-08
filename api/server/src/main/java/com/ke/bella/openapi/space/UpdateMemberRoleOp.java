package com.ke.bella.openapi.space;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;

/**
 * function: Update team member role
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRoleOp extends Operator {

    /**
     * Member UID
     */
    @NotEmpty(message = "memberUid cannot be empty")
    private String memberUid;

    /**
     * Space code
     */
    @NotEmpty(message = "spaceCode cannot be empty")
    private String spaceCode;

    /**
     * Role code
     */
    @NotEmpty(message = "roleCode cannot be empty")
    private String roleCode;

}
