package com.ke.bella.openapi.domain.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * function: Create role detail
 *
 * @author chenhongliang001
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleDetail {

    @NotEmpty(message = "roleCode cannot be empty")
    @Size(max = 64, message = "roleCode cannot exceed 64 characters")
    private String roleCode;

    @NotEmpty(message = "roleName cannot be empty")
    @Size(max = 64, message = "roleName cannot exceed 64 characters")
    private String roleName;

    @Size(max = 64, message = "roleDesc cannot exceed 64 characters")
    private String roleDesc;
}
