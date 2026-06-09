package com.ke.bella.openapi.modules.login;

import lombok.Data;

@Data
public class LoginProperties {

    private String loginPageUrl;

    private String authorizationHeader = "Authorization";
}
