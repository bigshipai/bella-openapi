package com.ke.bella.openapi.space;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * function:
 *
 * @author chenhongliang001
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpaceNameOp extends Operator {

    /**
     * Space code
     */
    @NotEmpty(message = "spaceCode cannot be empty")
    private String spaceCode;

    /**
     * Space name
     */
    @Size(max = 128, message = "spaceName cannot exceed 128 characters")
    private String spaceName;

}
