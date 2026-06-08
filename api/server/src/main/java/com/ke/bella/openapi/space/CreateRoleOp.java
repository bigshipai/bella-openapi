package com.ke.bella.openapi.space;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * function: Create role
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleOp extends Operator {

    /**
     * Space code
     */
    @NotEmpty(message = "spaceCode cannot be empty")
    @Size(max = 64, message = "spaceCode cannot exceed 64 characters")
    private String spaceCode;

    /**
     * Role list
     */
    @NotEmpty(message = "roles cannot be empty")
    private List<CreateRoleDetail> roles;

}
