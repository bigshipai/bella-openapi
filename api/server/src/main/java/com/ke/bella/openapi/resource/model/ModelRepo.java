package com.ke.bella.openapi.resource.model;

import com.ke.bella.openapi.resource.endpoint.Condition;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.resource.MetaDataOps;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.tables.pojos.ModelAuthorizerRelDB;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.tables.pojos.ModelDB;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.tables.pojos.ModelEndpointRelDB;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.tables.records.ModelAuthorizerRelRecord;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.tables.records.ModelEndpointRelRecord;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.tables.records.ModelRecord;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.apache.commons.collections4.CollectionUtils;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.apache.commons.lang3.StringUtils;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.jooq.Record;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.jooq.SelectJoinStep;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.jooq.SelectSeekStep1;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.jooq.TableField;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.jooq.impl.DSL;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.jooq.impl.TableImpl;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.springframework.stereotype.Component;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.springframework.transaction.annotation.Transactional;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import org.springframework.util.Assert;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;

import java.util.ArrayList;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import java.util.Collection;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import java.util.List;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import java.util.Set;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;

import static com.ke.bella.openapi.Tables.MODEL;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import static com.ke.bella.openapi.Tables.MODEL_AUTHORIZER_REL;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import static com.ke.bella.openapi.Tables.MODEL_ENDPOINT_REL;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import static com.ke.bella.openapi.common.constant.EntityConstants.ORG;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import static com.ke.bella.openapi.common.constant.EntityConstants.PERSON;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;
import static com.ke.bella.openapi.common.constant.EntityConstants.PUBLIC;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.db.repo.StatusRepo;
import com.ke.bella.openapi.db.repo.Page;

/**
 * Author: Stan Sai Date: 2024/8/1 20:34 description:
 */
@Component
public class ModelRepo extends StatusRepo<ModelDB, ModelRecord, String> {

    @Transactional
    public void updateVisibility(String modelName, String visibility) {
        ModelRecord rec = MODEL.newRecord();
        rec.setVisibility(visibility);
        fillUpdatorInfo(rec);
        int num = db.update(MODEL)
                .set(rec)
                .where(MODEL.MODEL_NAME.eq(modelName))
                .execute();
        Assert.isTrue(num == 1, "Model entity update failed, please verify model entity exists");
    }

    public List<ModelDB> listLinkedRelations() {
        return db.select(MODEL.MODEL_NAME, MODEL.LINKED_TO).from(MODEL)
                .fetchInto(ModelDB.class);
    }

    public List<ModelDB> list(Condition.ModelCondition op) {
        return constructSql(op).fetchInto(ModelDB.class);
    }

    public Page<ModelDB> page(Condition.ModelCondition op) {
        return queryPage(db, constructSql(op), op.getPage(), op.getSize(), ModelDB.class);
    }

    @SuppressWarnings("all")
    private SelectSeekStep1<Record, Long> constructSql(Condition.ModelCondition op) {
        SelectJoinStep step = db.select(MODEL.fields())
                .from(MODEL);
        if(StringUtils.isNotEmpty(op.getEndpoint()) || CollectionUtils.isNotEmpty(op.getEndpoints())) {
            step = step.leftJoin(MODEL_ENDPOINT_REL)
                    .on(MODEL.MODEL_NAME.eq(MODEL_ENDPOINT_REL.MODEL_NAME));
        }
        if(StringUtils.isNotEmpty(op.getPersonalCode()) || CollectionUtils.isNotEmpty(op.getOrgCodes())) {
            step = step.leftJoin(MODEL_AUTHORIZER_REL)
                    .on(MODEL.MODEL_NAME.eq(MODEL_AUTHORIZER_REL.MODEL_NAME));
        }

        org.jooq.Condition propertiesCondition = DSL.noCondition();
        if(op.getMaxInputTokensLimit() != null) {
            propertiesCondition = propertiesCondition.and(
                    DSL.field("JSON_EXTRACT({0}, '$.max_input_context') >= {1}", Boolean.class,
                            MODEL.PROPERTIES, op.getMaxInputTokensLimit()).isTrue());
        }
        if(op.getMaxOutputTokensLimit() != null) {
            propertiesCondition = propertiesCondition.and(
                    DSL.field("JSON_EXTRACT({0}, '$.max_output_context') >= {1}", Boolean.class,
                            MODEL.PROPERTIES, op.getMaxOutputTokensLimit()).isTrue());
        }

        org.jooq.Condition featuresCondition = DSL.noCondition();
        if(CollectionUtils.isNotEmpty(op.getFeatures())) {
            for (String feature : op.getFeatures()) {
                featuresCondition = featuresCondition.and(
                        MODEL.FEATURES.like("%\"" + feature + "\":true%"));
            }
        }

        return step
                .where(StringUtils.isEmpty(op.getModelName()) ? DSL.noCondition()
                        : op.isIncludeLinkedTo() ? MODEL.MODEL_NAME.like(op.getModelName() + "%").or(MODEL.LINKED_TO.like(op.getModelName() + "%"))
                                : MODEL.MODEL_NAME.like(op.getModelName() + "%"))
                .and(StringUtils.isEmpty(op.getOwnerName()) ? DSL.noCondition() : MODEL.OWNER_NAME.eq(op.getOwnerName()))
                .and(CollectionUtils.isEmpty(op.getModelNames()) ? DSL.noCondition()
                        : op.isIncludeLinkedTo() ? MODEL.MODEL_NAME.in(op.getModelNames()).or(MODEL.LINKED_TO.in(op.getModelNames()))
                                : MODEL.MODEL_NAME.in(op.getModelNames()))
                .and(StringUtils.isEmpty(op.getVisibility()) ? DSL.noCondition() : MODEL.VISIBILITY.eq(op.getVisibility()))
                .and(StringUtils.isEmpty(op.getStatus()) ? DSL.noCondition() : MODEL.STATUS.eq(op.getStatus()))
                .and(StringUtils.isEmpty(op.getEndpoint()) ? DSL.noCondition() : MODEL_ENDPOINT_REL.ENDPOINT.eq(op.getEndpoint()))
                .and(CollectionUtils.isEmpty(op.getEndpoints()) ? DSL.noCondition() : MODEL_ENDPOINT_REL.ENDPOINT.in(op.getEndpoints()))
                .and(permissionCondition(op.getPersonalCode(), op.getOrgCodes()))
                .and(propertiesCondition)
                .and(featuresCondition)
                .orderBy(MODEL.ID);
    }

    private org.jooq.Condition permissionCondition(String personalCode, Set<String> orgCodes) {
        if(StringUtils.isEmpty(personalCode) && CollectionUtils.isEmpty(orgCodes)) {
            return DSL.noCondition();
        }
        org.jooq.Condition condition = MODEL.VISIBILITY.eq(PUBLIC);
        if(StringUtils.isNotEmpty(personalCode)) {
            condition = condition.or(MODEL_AUTHORIZER_REL.AUTHORIZER_TYPE.eq(PERSON)
                    .and(MODEL_AUTHORIZER_REL.AUTHORIZER_CODE.eq(personalCode)));
        }
        if(CollectionUtils.isNotEmpty(orgCodes)) {
            condition = condition.or(MODEL_AUTHORIZER_REL.AUTHORIZER_TYPE.eq(ORG)
                    .and(MODEL_AUTHORIZER_REL.AUTHORIZER_CODE.in(orgCodes)));

        }
        return condition;
    }

    @Transactional
    public int batchInsertModelEndpoints(String modelName, Collection<String> endpoints) {
        List<ModelEndpointRelRecord> records = new ArrayList<>();
        for (String endpoint : endpoints) {
            ModelEndpointRelRecord rec = MODEL_ENDPOINT_REL.newRecord();
            rec.setModelName(modelName);
            rec.setEndpoint(endpoint);
            fillCreatorInfo(rec);
            records.add(rec);
        }
        return batchInsert(db, records);
    }

    @Transactional
    public int batchDeleteModelEndpoints(List<Long> ids) {
        return db.deleteFrom(MODEL_ENDPOINT_REL)
                .where(MODEL_ENDPOINT_REL.ID.in(ids))
                .execute();
    }

    public List<ModelEndpointRelDB> listEndpointsByModelName(String modelName) {
        return db.selectFrom(MODEL_ENDPOINT_REL)
                .where(MODEL_ENDPOINT_REL.MODEL_NAME.eq(modelName))
                .fetchInto(ModelEndpointRelDB.class);
    }

    public List<String> listModelNamesByEndpoint(String endpoint) {
        return db.selectFrom(MODEL_ENDPOINT_REL)
                .where(MODEL_ENDPOINT_REL.ENDPOINT.eq(endpoint))
                .fetch(MODEL_ENDPOINT_REL.MODEL_NAME);
    }

    public List<ModelEndpointRelDB> listEndpointsByModelNames(Set<String> modelNames) {
        return db.selectFrom(MODEL_ENDPOINT_REL)
                .where(MODEL_ENDPOINT_REL.MODEL_NAME.in(modelNames))
                .fetchInto(ModelEndpointRelDB.class);
    }

    public List<ModelDB> listAll() {
        return db.selectFrom(MODEL)
                .fetchInto(ModelDB.class);
    }

    @Transactional
    public int batchDeleteModelAuthorizers(List<Long> ids) {
        return db.deleteFrom(MODEL_AUTHORIZER_REL)
                .where(MODEL_AUTHORIZER_REL.ID.in(ids))
                .execute();
    }

    @Transactional
    public int batchInsertModelAuthorizers(String modelName, Collection<MetaDataOps.ModelAuthorizer> authorizers) {
        List<ModelAuthorizerRelRecord> records = new ArrayList<>();
        for (MetaDataOps.ModelAuthorizer authorizer : authorizers) {
            ModelAuthorizerRelRecord rec = MODEL_AUTHORIZER_REL.newRecord();
            rec.from(authorizer);
            rec.setModelName(modelName);
            fillCreatorInfo(rec);
            records.add(rec);
        }
        return batchInsert(db, records);
    }

    public List<ModelAuthorizerRelDB> listAuthorizersByModelName(String modelName) {
        return db.selectFrom(MODEL_AUTHORIZER_REL)
                .where(MODEL_AUTHORIZER_REL.MODEL_NAME.eq(modelName))
                .fetchInto(ModelAuthorizerRelDB.class);
    }

    public List<String> listModelNamesByAuthorizer(String authorizerType, String authorizerCode) {
        return db.selectFrom(MODEL_AUTHORIZER_REL)
                .where(MODEL_AUTHORIZER_REL.AUTHORIZER_TYPE.eq(authorizerType))
                .and(MODEL_AUTHORIZER_REL.AUTHORIZER_CODE.eq(authorizerCode))
                .fetch(MODEL_AUTHORIZER_REL.MODEL_NAME);
    }

    @Override
    public TableImpl<ModelRecord> table() {
        return MODEL;
    }

    @Override
    protected TableField<ModelRecord, String> uniqueKey() {
        return MODEL.MODEL_NAME;
    }

    @Override
    protected TableField<ModelRecord, String> statusFiled() {
        return MODEL.STATUS;
    }
}
