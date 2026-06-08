package com.ke.bella.openapi.common.model;

import lombok.Data;

@Data
public class PageCondition {
    private int page = 1;
    private int size = 10;
}
