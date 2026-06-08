package com.ke.bella.openapi.login.user;

import com.ke.bella.openapi.Operator;

public interface IUserRepo {
    Operator persist(Operator operator);

    Operator checkSecret(String secret);

    Operator checkPassword(String email, String password);

    Operator register(String email, String password, String userName);
}
