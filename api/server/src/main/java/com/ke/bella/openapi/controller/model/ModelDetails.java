package com.ke.bella.openapi.controller.model;

import com.ke.bella.openapi.common.dto.BaseDto;
import com.ke.bella.openapi.domain.channel.Channel;
import lombok.Data;

import java.util.List;

@Data
public class ModelDetails extends BaseDto {
    private Model model;
    private List<Channel> channels;
}
