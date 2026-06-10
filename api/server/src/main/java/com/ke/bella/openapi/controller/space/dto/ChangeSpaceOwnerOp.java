package com.ke.bella.openapi.controller.space.dto;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;

/**
 * function: Change space owner
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSpaceOwnerOp extends Operator {

    /**
     * Space code
     */
    @NotEmpty(message = "spaceCode cannot be empty")
    private String spaceCode;

    /**
     * New owner UID
     */
    @NotEmpty(message = "ownerUid cannot be empty")
    private String ownerUid;

}
