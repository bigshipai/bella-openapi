package com.ke.bella.openapi.login;

import lombok.Data;

@Data
public class LoginProperties {

    private String loginPageUrl;

    private String authorizationHeader = "Authorization";
}
