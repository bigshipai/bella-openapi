package com.ke.bella.openapi.resource.category;

import com.alicp.jetcache.anno.Cached;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.google.common.collect.Lists;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.google.common.collect.Sets;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.common.constant.EntityConstants;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.resource.category.CategoryRepo;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.resource.endpoint.Condition;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.resource.endpoint.Endpoint;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.resource.endpoint.EndpointCategoryTree;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.resource.MetaDataOps;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.generated.tables.pojos.CategoryDB;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import com.ke.bella.openapi.generated.tables.pojos.EndpointCategoryRelDB;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import org.apache.commons.collections4.CollectionUtils;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import org.springframework.stereotype.Component;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import org.springframework.transaction.annotation.Transactional;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import org.springframework.util.Assert;
import com.ke.bella.openapi.resource.endpoint.EndpointService;

import java.util.ArrayList;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import java.util.Arrays;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import java.util.List;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import java.util.Map;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import java.util.Set;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import java.util.stream.Collectors;
import com.ke.bella.openapi.resource.endpoint.EndpointService;

import static com.ke.bella.openapi.common.constant.EntityConstants.ACTIVE;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import static com.ke.bella.openapi.common.constant.EntityConstants.INACTIVE;
import com.ke.bella.openapi.resource.endpoint.EndpointService;
import static com.ke.bella.openapi.console.MetadataValidator.matchPath;
import com.ke.bella.openapi.resource.endpoint.EndpointService;

/**
 * Author: Stan Sai Date: 2024/8/2 12:00 description:
 */
@Component
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private EndpointService endpointService;

    @Transactional
    public CategoryDB createCategory(MetaDataOps.CategoryCreateOp op) {
        checkCategoryName(op.getParentCode(), op.getCategoryName());
        checkParentCode(op.getParentCode());
        checkCategoryName(op.getParentCode() == null ? "" : op.getParentCode(), op.getCategoryName());
        return categoryRepo.insert(op);
    }

    private void checkCategoryName(String parentCode, String name) {
        CategoryDB db = categoryRepo.queryByParentCodeAndName(parentCode, name);
        Assert.isNull(db, "Same category already exists under parent category");
    }

    private void checkParentCode(String parentCode) {
        if(parentCode == null) {
            return;
        }
        // 加锁，防止执行过程中，变成叶子节点
        CategoryDB existed = categoryRepo.queryByUniqueKeyForUpdate(parentCode);
        Assert.notNull(existed, "Parent category code does not exist");
        long endpointNum = categoryRepo.countEndpointCategoriesByCategoryCode(parentCode);
        Assert.isTrue(endpointNum == 0, "Category associated with endpoints cannot be used as parent");
    }

    @Transactional
    public void changeStatus(String categoryCode, boolean active) {
        categoryRepo.checkExist(categoryCode, true);
        String status = active ? ACTIVE : INACTIVE;
        categoryRepo.updateStatus(categoryCode, status);
    }

    public List<CategoryDB> listByCondition(Condition.CategoryCondition condition) {
        return categoryRepo.list(condition);
    }

    public Page<CategoryDB> pageByCondition(Condition.CategoryCondition condition) {
        return categoryRepo.page(condition);
    }

    @Transactional
    public void addCategoriesWithEndpoint(MetaDataOps.EndpointCategoriesOp op) {
        Set<String> categories = op.getCategoryCodes();
        categoryRepo.listEndpointCategoriesByEndpoint(op.getEndpoint())
                .stream()
                .map(EndpointCategoryRelDB::getCategoryCode)
                .forEach(categories::remove);
        if(categories.isEmpty()) {
            return;
        }
        checkLeafCategories(categories);
        categoryRepo.batchInsertRelations(op.getEndpoint(), op.getCategoryCodes());
    }

    @Transactional
    public void removeCategoriesWithEndpoint(MetaDataOps.EndpointCategoriesOp op) {
        List<Long> ids = categoryRepo.listEndpointCategoriesByEndpoint(op.getEndpoint())
                .stream()
                .filter(x -> op.getCategoryCodes().contains(x.getCategoryCode()))
                .map(EndpointCategoryRelDB::getId)
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(ids)) {
            categoryRepo.deleteRelations(ids);
        }
    }

    @Transactional
    public void replaceCategoryWithEndpoint(MetaDataOps.EndpointCategoriesOp op) {
        Set<String> systemCategory = Arrays.stream(EntityConstants.SystemBasicEndpoint.values())
                .filter(x -> matchPath(x.getEndpoint(), op.getEndpoint()))
                .map(EntityConstants.SystemBasicEndpoint::getCategory)
                .map(EntityConstants.SystemBasicCategory::getCode)
                .collect(Collectors.toSet());
        checkLeafCategories(op.getCategoryCodes());
        List<EndpointCategoryRelDB> orgin = categoryRepo.listEndpointCategoriesByEndpoint(op.getEndpoint());
        List<Long> deletes = new ArrayList<>();
        Set<String> insertCodes = Sets.newHashSet(op.getCategoryCodes());
        orgin.forEach(db -> {
            if(op.getCategoryCodes().contains(db.getCategoryCode())) {
                insertCodes.remove(db.getCategoryCode());
            } else if(!systemCategory.contains(db.getCategoryCode())) {
                deletes.add(db.getId());
            }
        });
        if(CollectionUtils.isNotEmpty(deletes)) {
            categoryRepo.deleteRelations(deletes);
        }
        if(CollectionUtils.isNotEmpty(insertCodes)) {
            categoryRepo.batchInsertRelations(op.getEndpoint(), insertCodes);
        }
    }

    private void checkLeafCategories(Set<String> categoryCodes) {
        // 当一次性要关联多个节点时，获取其中一个锁失败时不等待，直接抛异常，防止死锁发生
        boolean nowait = categoryCodes.size() > 1;
        categoryCodes.forEach(code -> checkLeafCategory(code, nowait));
    }

    private void checkLeafCategory(String categoryCode, boolean nowait) {
        // 加锁，防止执行过程中变成了父节点
        CategoryDB existed = nowait ? categoryRepo.queryByUniqueKeyForUpdateNoWait(categoryCode)
                : categoryRepo.queryByUniqueKeyForUpdate(categoryCode);
        Assert.notNull(existed, "Category code does not exist: " + categoryCode);
        long childs = categoryRepo.count(Condition.CategoryCondition.builder().parentCode(categoryCode).build());
        Assert.isTrue(childs == 0, categoryCode + " Already used as parent category, cannot associate with endpoints");
    }

    public EndpointCategoryTree listTree(Condition.CategoryTreeCondition condition) {
        List<CategoryDB> categories = categoryRepo.queryAllChildrenIncludeSelfByCategoryCode(condition.getCategoryCode(),
                condition.getStatus());
        CategoryDB category = categories.stream().filter(db -> db.getCategoryCode().equals(condition.getCategoryCode())).findAny()
                .orElse(null);
        if(category == null) {
            return null;
        }
        return getTreeByCategoryCode(category,
                categories.stream().collect(Collectors.groupingBy(CategoryDB::getParentCode)),
                condition.isIncludeEndpoint());
    }

    @Cached(name = "category:tree:", key = "'all'")
    public List<EndpointCategoryTree> listAllTree() {
        List<CategoryDB> topCategories = categoryRepo.list(Condition.CategoryCondition.builder()
                .status(ACTIVE).topCategory(true).build());
        List<EndpointCategoryTree> result = new ArrayList<>();
        for (CategoryDB top : topCategories) {
            List<CategoryDB> categories = categoryRepo.queryAllChildrenIncludeSelfByCategoryCode(top.getCategoryCode(), ACTIVE);
            categories.stream().filter(db -> db.getCategoryCode().equals(top.getCategoryCode())).findAny().ifPresent(
                    category -> result.add(
                            getTreeByCategoryCode(category, categories.stream().collect(Collectors.groupingBy(CategoryDB::getParentCode)), true)));
        }
        return result;
    }

    private EndpointCategoryTree getTreeByCategoryCode(CategoryDB root, Map<String, List<CategoryDB>> categories, boolean includeEndpoint) {
        EndpointCategoryTree tree = new EndpointCategoryTree();
        tree.setCategoryCode(root.getCategoryCode());
        tree.setCategoryName(root.getCategoryName());
        List<CategoryDB> children = categories.get(root.getCategoryCode());
        if(CollectionUtils.isEmpty(children)) {
            if(includeEndpoint) {
                tree.setEndpoints(listEndpointByCategoryCode(root.getCategoryCode()));
            }
            return tree;
        }
        for (CategoryDB child : children) {
            tree.addChild(getTreeByCategoryCode(child, categories, includeEndpoint));
        }
        return tree;
    }

    private List<Endpoint> listEndpointByCategoryCode(String categoryCode) {
        List<EndpointCategoryRelDB> relations = categoryRepo.listEndpointCategoriesByCategoryCodes(Lists.newArrayList(categoryCode));
        if(CollectionUtils.isEmpty(relations)) {
            return Lists.newArrayList();
        }
        Set<String> endpoints = relations.stream().map(EndpointCategoryRelDB::getEndpoint).collect(Collectors.toSet());
        return endpointService.listByCondition(Condition.EndpointCondition.builder()
                .endpoints(endpoints).build(), Endpoint.class);
    }
}
