package com.ke.bella.openapi.domain.common;

import org.jooq.TableField;
import org.jooq.impl.UpdatableRecordImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Author: Stan Sai Date: 2024/8/8 00:39 description:
 */
public abstract class StatusRepo<T, R extends UpdatableRecordImpl<R>, K> extends UniqueKeyRepo<T, R, K> {
    protected abstract TableField<R, String> statusFiled();

    @Transactional
    public void updateStatus(K categoryCode, String status) {
        R rec = table().newRecord();
        rec.set(statusFiled(), status);
        int num = db.update(table())
                .set(rec)
                .where(uniqueKey().eq(categoryCode))
                .execute();
        Assert.isTrue(num == 1, "Category entity update failed, please verify category entity exists");
    }
}
