package com.ke.bella.openapi.resource.model;

import com.ke.bella.openapi.common.dto.BaseDto;
import com.ke.bella.openapi.resource.channel.Channel;
import lombok.Data;

import java.util.List;

@Data
public class ModelDetails extends BaseDto {
    private Model model;
    private List<Channel> channels;
}
