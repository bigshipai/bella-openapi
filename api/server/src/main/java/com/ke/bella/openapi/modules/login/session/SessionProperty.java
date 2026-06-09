package com.ke.bella.openapi.modules.login.session;

import lombok.Data;

@Data
public class SessionProperty {

    private String sessionPrefix;

    private Integer maxInactiveInterval;

    private String authTokenHeader = "X-Auth-Token";
}
