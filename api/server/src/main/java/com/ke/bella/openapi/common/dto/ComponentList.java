package com.ke.bella.openapi.common.dto;

import java.util.List;

public interface ComponentList<E> extends List<E> {
    Class<E> getComponentType();
}
