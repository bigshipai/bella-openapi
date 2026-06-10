package com.ke.bella.openapi.controller.login.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.common.response.OneTokenResponse;
import com.ke.bella.openapi.controller.login.LoginProperties;
import com.ke.bella.openapi.controller.login.session.SessionManager;

import static com.ke.bella.openapi.config.OneTokenLoginConfiguration.redirectParameter;

/**
 * 认证 Controller，处理登录、注册、登出和用户信息查询。
 */
@RestController
@RequestMapping("/openapi")
public class AuthController {

    private final LoginProperties properties;
    private final SessionManager sessionManager;
    public static final String REDIRECT_HEADER = "X-Redirect-Login";

    public AuthController(LoginProperties properties, SessionManager sessionManager) {
        this.properties = properties;
        this.sessionManager = sessionManager;
    }

    /**
     * 登录 — 支持邮箱密码和密钥两种方式
     */
    @PostMapping("/login")
    public OneTokenResponse<Object> login(@RequestBody Map<String, Object> body,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        OneTokenResponse<Object> resp = new OneTokenResponse<>();

        if (!sessionManager.userRepoInitialized()) {
            resp.setCode(503);
            resp.setMessage("Login feature not initialized");
            return resp;
        }

        // 邮箱密码登录
        if (body.get("email") != null && body.get("password") != null) {
            String id = sessionManager.createByPassword(
                    body.get("email").toString(), body.get("password").toString(), request);
            if (id != null) {
                resp.setCode(200);
                Map<String, String> data = new HashMap<>();
                data.put("token", id);
                resp.setData(data);
                return resp;
            }
            resp.setCode(400);
            resp.setMessage("Incorrect email or password");
            return resp;
        }

        // 密钥登录
        if (body.get("secret") != null) {
            String id = sessionManager.create(body.get("secret").toString(), request);
            if (id != null) {
                resp.setCode(200);
                Map<String, String> data = new HashMap<>();
                data.put("token", id);
                resp.setData(data);
                return resp;
            }
        }

        resp.setCode(400);
        resp.setMessage("Missing login parameters");
        return resp;
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public OneTokenResponse<Object> register(@RequestBody Map<String, Object> body,
                                             HttpServletRequest request) {
        OneTokenResponse<Object> resp = new OneTokenResponse<>();

        if (!sessionManager.userRepoInitialized()) {
            resp.setCode(503);
            resp.setMessage("Registration feature not initialized");
            return resp;
        }

        String email = body.get("email") != null ? body.get("email").toString() : null;
        String password = body.get("password") != null ? body.get("password").toString() : null;
        String userName = body.get("userName") != null ? body.get("userName").toString() : null;

        if (email != null && password != null) {
            Operator operator = sessionManager.register(email, password, userName);
            if (operator != null) {
                // 注册成功，自动登录
                String token = sessionManager.createByPassword(email, password, request);
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", operator);
                resp.setCode(200);
                resp.setData(data);
                return resp;
            }
            resp.setCode(400);
            resp.setMessage("Registration failed, email may already be in use");
            return resp;
        }

        resp.setCode(400);
        resp.setMessage("Missing registration parameters (email, password)");
        return resp;
    }

    /**
     * 登出
     */
    @RequestMapping("/logout")
    public OneTokenResponse<Void> logout(HttpServletRequest request) {
        sessionManager.destroySession(request);
        OneTokenResponse<Void> resp = new OneTokenResponse<>();
        resp.setCode(HttpStatus.OK.value());
        return resp;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userInfo")
    public OneTokenResponse<Operator> userInfo(HttpServletRequest request,
                                                HttpServletResponse response) {
        OneTokenResponse<Operator> resp = new OneTokenResponse<>();
        Operator operator = sessionManager.getSession(request);
        if (operator != null) {
            resp.setCode(200);
            resp.setData(operator);
            return resp;
        }
        // 未认证，设置重定向 header
        response.setHeader(REDIRECT_HEADER,
                properties.getLoginPageUrl() + "?" + redirectParameter + "=");
        resp.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        return resp;
    }
}
