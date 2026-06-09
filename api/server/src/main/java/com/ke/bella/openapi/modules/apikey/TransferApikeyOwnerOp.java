package com.ke.bella.openapi.modules.apikey;

import com.ke.bella.openapi.common.model.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotEmpty;

/**
 * API Key ownership transfer operation request
 *
 * @author claude
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TransferApikeyOwnerOp extends Operator {

    /**
     * API Key code
     */
    @NotEmpty(message = "API Key code cannot be empty")
    private String akCode;

    /**
     * Target user ID (optional, alternative to other fields)
     */
    private Long targetUserId;

    /**
     * Target user source (e.g.: github, google, cas, etc.)
     */
    private String targetUserSource;

    /**
     * Target user source ID (i.e. source_id, which is owner_code)
     */
    private String targetUserSourceId;

    /**
     * Target user email (used together with targetUserSource)
     */
    private String targetUserEmail;

    /**
     * Transfer reason
     */
    private String transferReason;
}
