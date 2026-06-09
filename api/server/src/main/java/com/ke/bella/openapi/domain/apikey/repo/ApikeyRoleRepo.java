package com.ke.bella.openapi.domain.apikey.repo;

import com.ke.bella.openapi.domain.common.AutogenCodeRepo;
import com.ke.bella.openapi.domain.common.UniqueKeyRepo;
import com.ke.bella.openapi.jooqgen.tables.pojos.ApikeyRoleDB;
import com.ke.bella.openapi.jooqgen.tables.records.ApikeyRoleRecord;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Component;

import static com.ke.bella.openapi.jooqgen.Tables.APIKEY_ROLE;

@Component
public class ApikeyRoleRepo extends UniqueKeyRepo<ApikeyRoleDB, ApikeyRoleRecord, String> implements AutogenCodeRepo<ApikeyRoleRecord> {

    @Override
    public TableField<ApikeyRoleRecord, String> autoCode() {
        return APIKEY_ROLE.ROLE_CODE;
    }

    @Override
    public String prefix() {
        return "role-";
    }

    @Override
    protected TableImpl<ApikeyRoleRecord> table() {
        return APIKEY_ROLE;
    }

    @Override
    protected TableField<ApikeyRoleRecord, String> uniqueKey() {
        return APIKEY_ROLE.ROLE_CODE;
    }
}
