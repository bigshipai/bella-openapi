package com.ke.bella.openapi.db.repo;

import com.ke.bella.openapi.common.context.OneTokenContext;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Query;
import org.jooq.SelectLimitStep;
import org.jooq.UpdatableRecord;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.Arrays;
import java.util.Collection;

/**
 * Author: Stan Sai Date: 2024/8/8 00:55 description:
 */
public interface BaseRepo {

    /**
     * 填充创建人信息（cuid, cu_name），同时填充更新人信息（muid, mu_name）
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <R extends UpdatableRecordImpl<R>> void fillCreatorInfo(R rec) {
        com.ke.bella.openapi.common.model.Operator oper = OneTokenContext.getOperatorIgnoreNull();
        if (oper != null) {
            if (oper.getUserId() != null) {
                Field field = rec.field("cuid");
                if (field != null) {
                    rec.set(field, oper.getUserId());
                }
            }
            if (StringUtils.isNotEmpty(oper.getUserName())) {
                Field field = rec.field("cu_name");
                if (field != null) {
                    rec.set(field, oper.getUserName());
                }
            }
        }
        fillUpdatorInfo(rec);
    }

    /**
     * 填充更新人信息（muid, mu_name）
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <R extends UpdatableRecordImpl<R>> void fillUpdatorInfo(R rec) {
        com.ke.bella.openapi.common.model.Operator oper = OneTokenContext.getOperatorIgnoreNull();
        if (oper != null) {
            if (oper.getUserId() != null) {
                Field field = rec.field("muid");
                if (field != null) {
                    rec.set(field, oper.getUserId());
                }
            }
            if (StringUtils.isNotEmpty(oper.getUserName())) {
                Field field = rec.field("mu_name");
                if (field != null) {
                    rec.set(field, oper.getUserName());
                }
            }
        }
    }

    default int batchExecuteQuery(DSLContext db, Collection<Query> queries) {
        int[] rows = db.batch(queries).execute();
        int sum = Arrays.stream(rows).sum();
        if (sum < queries.size()) {
            throw new IllegalStateException("Batch processing failed");
        }
        return sum;
    }

    default int batchInsert(DSLContext db, Collection<? extends UpdatableRecord<?>> records) {
        int[] rows = db.batchInsert(records).execute();
        int sum = Arrays.stream(rows).sum();
        if (sum < records.size()) {
            throw new IllegalStateException("Batch processing failed");
        }
        return sum;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    default <T> Page<T> queryPage(DSLContext db, SelectLimitStep scs, int page, int pageSize, Class<?> type) {
        if (scs == null) {
            return Page.from(page, pageSize);
        }
        return Page.from(page, pageSize)
                .total(db.fetchCount(scs))
                .list(scs.limit((page - 1) * pageSize, pageSize)
                        .fetch()
                        .into(type));
    }
}
