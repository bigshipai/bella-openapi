package com.ke.bella.openapi.controller.space.dto;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;

/**
 * function: Exit space
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ExitSpaceOp extends Operator {

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

}
