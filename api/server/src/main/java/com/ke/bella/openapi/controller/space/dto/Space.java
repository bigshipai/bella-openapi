package com.ke.bella.openapi.controller.space.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * function:
 *
 * @author chenhongliang001
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Space {

    private String spaceCode;

    private String spaceName;

    private String ownerUid;
}
