package com.ke.bella.openapi.modules.login.user;

import com.ke.bella.openapi.common.model.Operator;

public interface IUserRepo {
    Operator persist(Operator operator);

    Operator checkSecret(String secret);

    Operator checkPassword(String email, String password);

    Operator register(String email, String password, String userName);
}
