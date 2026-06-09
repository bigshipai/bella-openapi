package com.ke.bella.openapi.modules.login.session;

import com.ke.bella.openapi.common.model.Operator;

import jakarta.servlet.http.HttpServletRequest;

public interface SessionManager {

    String create(Operator sessionInfo, HttpServletRequest request);

    String create(String secret, HttpServletRequest request);

    String createByPassword(String email, String password, HttpServletRequest request);

    Operator register(String email, String password, String userName);

    Operator getSession(HttpServletRequest request);

    void destroySession(HttpServletRequest request);

    void renew(HttpServletRequest request);

    boolean userRepoInitialized();
}
