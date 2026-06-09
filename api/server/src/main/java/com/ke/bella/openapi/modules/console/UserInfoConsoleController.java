package com.ke.bella.openapi.modules.console;

import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.common.annotation.OneTokenAPI;
import com.ke.bella.openapi.common.exception.BizParamCheckException;
import com.ke.bella.openapi.db.repo.UserRepo;
import com.ke.bella.openapi.generated.tables.pojos.UserDB;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OneTokenAPI
@RestController
@RequestMapping("/console/userInfo")
@Tag(name = "User Info Management")
public class UserInfoConsoleController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public Operator whoami() {
        return OneTokenContext.getOperator();
    }

    @PostMapping("/manager")
    public UserDB addManager(@RequestBody Operator op) {
        Assert.isTrue(
                (op.getUserId() != null && op.getUserId() > 0)
                        || (StringUtils.hasText(op.getSource()) && (StringUtils.hasText(op.getEmail()) || StringUtils.hasText(op.getSourceId()))),
                "invalid params");
        UserDB user;
        if(op.getUserId() != null && op.getUserId() > 0) {
            user = userRepo.addManagerById(op.getUserId());
        } else if(StringUtils.hasText(op.getSourceId())) {
            user = userRepo.addManagerBySourceAndSourceId(op.getSource(), op.getSourceId());
        } else {
            user = userRepo.addManagerBySourceAndEmail(op.getSource(), op.getEmail());
        }
        if(user == null) {
            throw new BizParamCheckException("User does not exist");
        }
        return user;
    }
}
