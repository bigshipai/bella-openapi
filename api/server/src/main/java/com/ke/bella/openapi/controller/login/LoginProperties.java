package com.ke.bella.openapi.controller.login;

import lombok.Data;

@Data
public class LoginProperties {

    private String loginPageUrl;

    private String authorizationHeader = "Authorization";
}
