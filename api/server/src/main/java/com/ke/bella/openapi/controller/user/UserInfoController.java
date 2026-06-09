package com.ke.bella.openapi.controller.user;

import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.common.annotation.OneTokenAPI;
import com.ke.bella.openapi.domain.user.UserRepo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@OneTokenAPI
@RestController
@RequestMapping("/v1/userInfo")
@Tag(name = "User Info Query")
public class UserInfoController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public Operator whoami() {
        return OneTokenContext.getOperator();
    }

    @GetMapping("/search")
    public List<UserSearchResult> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean excludeSelf) {

        Assert.isTrue(StringUtils.isNotBlank(keyword), "Search keyword cannot be empty");
        Assert.isTrue(limit > 0 && limit <= 100, "Return count must be between 1 and 100");

        Operator operator = OneTokenContext.getOperator();

        if(!excludeSelf) {
            return userRepo.searchUsers(keyword.trim(), limit);
        }

        Long currentUserId = operator.getUserId();

        if(currentUserId.toString().equals(operator.getSourceId())) {
            return userRepo.searchUsers(keyword.trim(), limit, null, currentUserId.toString());
        }
        return userRepo.searchUsers(keyword.trim(), limit, currentUserId, null);
    }
}
