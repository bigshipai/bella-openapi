package com.ke.bella.openapi.controller.space;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;

/**
 * function: Remove team member
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveMemberOp extends Operator {

    /**
     * Member UID
     */
    @NotEmpty(message = "memberUid cannot be empty")
    private String memberUid;

    /**
     * Code
     */
    @NotEmpty(message = "spaceCode cannot be empty")
    private String spaceCode;

}
