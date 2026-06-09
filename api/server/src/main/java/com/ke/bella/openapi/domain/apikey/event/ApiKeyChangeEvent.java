package com.ke.bella.openapi.domain.apikey.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyChangeEvent {
    private List<String> akCodes;
}
