package com.ke.bella.openapi.controller.space.dto;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * function: Create space parameters
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateSpaceOp extends Operator {

    /**
     * Space name
     */
    @NotEmpty(message = "spaceName cannot be empty")
    @Size(max = 128, message = "spaceName cannot exceed 128 characters")
    private String spaceName;

    /**
     * Space description
     */
    @Size(max = 255, message = "spaceDescription cannot exceed 255 characters")
    private String spaceDescription;

    /**
     * Space code
     */
    @Size(max = 64, message = "spaceCode cannot exceed 64 characters")
    private String spaceCode;

    /**
     * Space owner UID
     */
    @NotEmpty(message = "ownerUid cannot be empty")
    @Size(max = 64, message = "ownerUid cannot exceed 64 characters")
    private String ownerUid;

    /**
     * Space owner name
     */
    @NotEmpty(message = "ownerName cannot be empty")
    @Size(max = 64, message = "ownerName cannot exceed 64 characters")
    private String ownerName;

}
