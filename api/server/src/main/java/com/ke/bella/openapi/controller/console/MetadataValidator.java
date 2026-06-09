package com.ke.bella.openapi.controller.console;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ke.bella.openapi.controller.meta.dto.MetaDataOps;
import com.ke.bella.openapi.utils.JacksonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ke.bella.openapi.common.constant.EntityConstants.AUTHORIZER_TYPES;
import static com.ke.bella.openapi.common.constant.EntityConstants.CHANNEL_PRIORITY;
import static com.ke.bella.openapi.common.constant.EntityConstants.DATA_DESTINATIONS;
import static com.ke.bella.openapi.common.constant.EntityConstants.ENDPOINT;
import static com.ke.bella.openapi.common.constant.EntityConstants.MODEL;
import static com.ke.bella.openapi.common.constant.EntityConstants.ModelJsonKey;
import static com.ke.bella.openapi.common.constant.EntityConstants.OWNER_TYPES;
import static com.ke.bella.openapi.common.constant.EntityConstants.PRIVATE;
import static com.ke.bella.openapi.common.constant.EntityConstants.PUBLIC;
import static com.ke.bella.openapi.common.constant.EntityConstants.SystemBasicCategory;
import static com.ke.bella.openapi.common.constant.EntityConstants.SystemBasicEndpoint;
import static com.ke.bella.openapi.utils.MatchUtils.isAllText;
import static com.ke.bella.openapi.utils.MatchUtils.isBracesWithSpaces;
import static com.ke.bella.openapi.utils.MatchUtils.isTextStart;
import static com.ke.bella.openapi.utils.MatchUtils.isValidURL;

public class MetadataValidator {
    private static final LoadingCache<String, Pattern> baseEndpointPatternCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, Pattern>() {
                @Override
                public Pattern load(String key) {
                    String regex = StringUtils.replace(key, "*", "\\d+");
                    return Pattern.compile(regex);
                }
            });

    public static void checkEndpointOp(MetaDataOps.EndpointOp op, boolean create) {
        Assert.hasText(op.getEndpoint(), "Endpoint path cannot be empty");
        boolean systemEndpoint = Arrays.stream(SystemBasicEndpoint.values())
                .map(SystemBasicEndpoint::getEndpoint)
                .anyMatch(path -> matchPath(path, op.getEndpoint()));
        if(create) {
            Assert.isTrue(!systemEndpoint, "Cannot create system endpoint");
            Assert.isTrue(op.getEndpoint().matches(".*/v\\d+/.*"), "Path format must be */v{d}/*");
            Assert.isTrue(!op.getEndpoint().contains("_"), "Endpoint path cannot contain underscores");
            Assert.isTrue(op.getEndpoint().length() <= 60, "Endpoint path length cannot exceed 60");
            Assert.hasText(op.getEndpointName(), "Endpoint name cannot be empty");
            Assert.hasText(op.getMaintainerCode(), "Maintainer system ID cannot be empty");
            Assert.hasText(op.getMaintainerName(), "Maintainer name cannot be empty");
        } else {
            if(systemEndpoint) {
                op.setEndpointName(null);
            }
            Assert.isTrue(StringUtils.isNotBlank(op.getEndpointName())
                    || StringUtils.isNotBlank(op.getMaintainerCode())
                    || StringUtils.isNotBlank(op.getMaintainerName())
                    || StringUtils.isNotBlank(op.getDocumentUrl()), "All modifiable fields are empty, no changes to make");
        }
        if(StringUtils.isNotEmpty(op.getEndpointName())) {
            Assert.isTrue(isTextStart(op.getEndpointName()), "Endpoint name cannot start with numbers/special characters/spaces");
            Assert.isTrue(op.getEndpointName().length() <= 10, "Endpoint name length cannot exceed 10");
        }
        if(StringUtils.isNotEmpty(op.getMaintainerCode())) {
            Assert.isTrue(StringUtils.isNumeric(op.getMaintainerCode()), "System ID must be numeric");
        }
        if(StringUtils.isNotEmpty(op.getMaintainerName())) {
            Assert.isTrue(isAllText(op.getMaintainerName()), "Name cannot contain numbers/special characters/spaces");
        }
        if(StringUtils.isNotEmpty(op.getDocumentUrl())) {
            Assert.isTrue(isValidURL(op.getDocumentUrl()), "Document URL must be a valid URL");
            Assert.isTrue(op.getDocumentUrl().length() <= 255, "Document URL length cannot exceed 255");
        }
    }

    public static void checkEndpointStatusOp(MetaDataOps.EndpointStatusOp op) {
        Assert.isTrue(Arrays.stream(SystemBasicEndpoint.values()).map(SystemBasicEndpoint::getEndpoint)
                .noneMatch(path -> matchPath(path, op.getEndpoint())), "Cannot modify system endpoint");
        Assert.hasText(op.getEndpoint(), "Endpoint path cannot be empty");
    }

    public static void checkModelOp(MetaDataOps.ModelOp op, boolean create) {
        Assert.hasText(op.getModelName(), "Model name cannot be empty");
        if(create) {
            Assert.isTrue(isTextStart(op.getModelName()), "Model name cannot start with numbers/special characters/spaces");
            Assert.notEmpty(op.getEndpoints(), "Endpoints cannot be empty");
            Assert.isTrue(op.getModelName().length() <= 60, "Model name length cannot exceed 60");
            Assert.hasText(op.getProperties(), "Model properties cannot be empty");
            Assert.hasText(op.getFeatures(), "Model features cannot be empty");
            Assert.hasText(op.getOwnerType(), "Model owner type cannot be empty");
            Assert.hasText(op.getOwnerCode(), "Model owner code cannot be empty");
            Assert.hasText(op.getOwnerName(), "Model owner name cannot be empty");
        } else {
            Assert.isTrue(CollectionUtils.isNotEmpty(op.getEndpoints())
                    || StringUtils.isNotBlank(op.getDocumentUrl())
                    || StringUtils.isNotBlank(op.getProperties())
                    || StringUtils.isNotBlank(op.getFeatures())
                    || op.getOpennessType() != null, "All modifiable fields are empty, no changes to make");
        }
        if(StringUtils.isNotEmpty(op.getOwnerType())) {
            Assert.isTrue(OWNER_TYPES.contains(op.getOwnerType()), "Invalid owner type");
        }
    }

    public static void checkModelNameOp(MetaDataOps.ModelNameOp op) {
        Assert.hasText(op.getModelName(), "Model name cannot be empty");
    }

    public static void checkModelLinkOp(MetaDataOps.ModelLinkOp op) {
        Assert.hasText(op.getModelName(), "Model name cannot be empty");
        Assert.notNull(op.getLinkedTo(), "Model linkTo cannot be null");
        Assert.isTrue(!op.getLinkedTo().equals(op.getModelName()), "Circular link");
    }

    public static void checkModelAuthorizerOp(MetaDataOps.ModelAuthorizerOp op) {
        Assert.hasText(op.getModel(), "Model name cannot be empty");
        if(CollectionUtils.isNotEmpty(op.getAuthorizers())) {
            Assert.isTrue(op.getAuthorizers().stream().allMatch(x -> AUTHORIZER_TYPES.contains(x.getAuthorizerType())),
                    "Invalid authorizer type");
            Assert.isTrue(op.getAuthorizers().stream().map(MetaDataOps.ModelAuthorizer::getAuthorizerCode).noneMatch(String::isEmpty),
                    "Authorizer code cannot be empty");
            Assert.isTrue(op.getAuthorizers().stream().map(MetaDataOps.ModelAuthorizer::getAuthorizerName).noneMatch(String::isEmpty),
                    "Authorizer name cannot be empty");
        }
    }

    public static void checkChannelCreateOp(MetaDataOps.ChannelCreateOp op) {
        Assert.hasText(op.getEntityType(), "Entity type cannot be empty");
        Assert.hasText(op.getEntityCode(), "Entity code cannot be empty");
        if(op.getEntityType().equals(MODEL)) {
            Assert.hasText(op.getChannelInfo(), "Channel info for model channel cannot be empty");
            Assert.hasText(op.getPriceInfo(), "Price info for model channel cannot be empty");
        }
        Assert.isTrue(op.getEntityType().equals(ENDPOINT) || op.getEntityType().equals(MODEL),
                "Entity type must be endpoint or model");
        Assert.hasText(op.getProtocol(), "Request protocol cannot be empty");
        Assert.hasText(op.getSupplier(), "Supplier cannot be empty");
        Assert.hasText(op.getUrl(), "URL cannot be empty");
        Assert.isTrue(DATA_DESTINATIONS.contains(op.getDataDestination()),
                "Channel data direction must be: " + String.join(" or ", DATA_DESTINATIONS));
        Assert.isTrue(CHANNEL_PRIORITY.contains(op.getPriority()),
                "Channel priority must be: " + String.join(" or ", CHANNEL_PRIORITY));
        Assert.isTrue(op.getTrialEnabled() == 1 || op.getTrialEnabled() == 0, "Trial switch must be 0 or 1");
        Assert.hasText(op.getProtocol(), "Request protocol cannot be empty string");
        Assert.hasText(op.getSupplier(), "Supplier cannot be empty string");

        // Validate visibility if provided
        if(StringUtils.isNotEmpty(op.getVisibility())) {
            Assert.isTrue(PRIVATE.equals(op.getVisibility()) ||
                    PUBLIC.equals(op.getVisibility()),
                    "Channel visibility must be: " + PRIVATE + " or " + PUBLIC);

            // If it's a private channel, validate owner information
            if(PRIVATE.equals(op.getVisibility())) {
                Assert.hasText(op.getOwnerType(), "Private channel owner type cannot be empty");
                Assert.hasText(op.getOwnerCode(), "Private channel owner code cannot be empty");
                Assert.isTrue(OWNER_TYPES.contains(op.getOwnerType()),
                        "Owner type must be: " + String.join(" or ", OWNER_TYPES));
            }
        }

        checkJsonInfo(op.getChannelInfo());
        checkJsonInfo(op.getPriceInfo());
    }

    public static void checkChannelUpdateOp(MetaDataOps.ChannelUpdateOp op) {
        Assert.hasText(op.getChannelCode(), "Channel code cannot be empty");
        Assert.isTrue(StringUtils.isNotBlank(op.getPriority())
                || StringUtils.isNotBlank(op.getChannelInfo())
                || StringUtils.isNotBlank(op.getPriceInfo())
                || StringUtils.isNotBlank(op.getQueueName())
                || op.getQueueMode() != null, "All modifiable fields are empty, no changes to make");
        if(op.getPriority() != null) {
            Assert.isTrue(CHANNEL_PRIORITY.contains(op.getPriority()),
                    "Channel priority must be: " + String.join(" or ", CHANNEL_PRIORITY));
        }
        Assert.isTrue(op.getTrialEnabled() == null || op.getTrialEnabled() == 1 || op.getTrialEnabled() == 0, "Trial switch must be 0 or 1");
        checkJsonInfo(op.getChannelInfo());
        checkJsonInfo(op.getPriceInfo());
    }

    private static void checkJsonInfo(String info) {
        // 只检查是否是json，其他信息在service中根据类型判断
        if(info != null) {
            Map<String, Object> map = json2Map(info);
            Assert.isTrue(map == null || !map.isEmpty(), "Info is not in JSON format");
        }
    }

    public static void checkChannelStatusOp(MetaDataOps.ChannelStatusOp op) {
        Assert.hasText(op.getChannelCode(), "Channel code cannot be empty");
    }

    public static void checkCategoryCreateOp(MetaDataOps.CategoryCreateOp op) {
        if(op.getParentCode() != null) {
            Assert.hasText(op.getParentCode(), "Parent category code cannot be empty string");
        }
        Assert.isTrue(Arrays.stream(SystemBasicCategory.values())
                .noneMatch(x -> {
                    String systemParentCode = x.getParent() == null ? "" : x.getParent().getCode();
                    String parentCode = op.getParentCode() == null ? "" : op.getParentCode();
                    return parentCode.equals(systemParentCode) && op.getCategoryName().equals(x.getName());
                }),
                "Cannot create system category");
        Assert.hasText(op.getCategoryName(), "Category name cannot be empty");
        Assert.isTrue(op.getCategoryName().length() <= 10, "Category name length cannot exceed 10");
        Assert.isTrue(isTextStart(op.getCategoryName()), "Category name must start with a letter");
    }

    public static void checkCategoryStatus(MetaDataOps.CategoryStatusOp op) {
        Assert.hasText(op.getCategoryCode(), "Category code cannot be empty");
    }

    public static void checkEndpointCategoryOp(MetaDataOps.EndpointCategoriesOp op) {
        Assert.hasText(op.getEndpoint(), "Endpoint path cannot be empty");
        Assert.notEmpty(op.getCategoryCodes(), "Category code cannot be empty");
        Assert.isTrue(Arrays.stream(SystemBasicEndpoint.values())
                .noneMatch(endpoint -> matchPath(endpoint.getEndpoint(), op.getEndpoint())
                        && op.getCategoryCodes().contains(endpoint.getCategory().getCode())),
                "Cannot modify system default endpoint categories");
    }

    public static void checkReplaceEndpointCategoryOp(MetaDataOps.EndpointCategoriesOp op) {
        Assert.hasText(op.getEndpoint(), "Endpoint path cannot be empty");
        if(CollectionUtils.isNotEmpty(op.getCategoryCodes())) {
            op.getCategoryCodes().forEach(code -> Assert.hasText(code, "Category code cannot be empty"));
        }
    }

    /**
     * 检查路径是否匹配
     *
     * @param match 要匹配的路径
     * @param path  要检查的理解
     *
     * @return 匹配返回true，不匹配返回false
     */
    public static boolean matchPath(String match, String path) {
        try {
            Matcher matcher = baseEndpointPatternCache.get(match).matcher(path);
            return matcher.matches();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> json2Map(String jsonStr) {
        if(isBracesWithSpaces(jsonStr)) {
            return null;
        }
        return JacksonUtils.toMap(jsonStr);
    }

    /**
     * 检查json是否符合要求
     *
     * @param map      jsonMap
     * @param endpoint 能力点，对应 {@link ModelJsonKey} 中的endpoint
     * @param field    model实体的字段名，对应 {@link ModelJsonKey} 中的field
     *
     * @return 如果存在不符合要求的key，返回不合法的提示，否则返回null
     */
    public static String generateInvalidModelJsonKeyMessage(Map<String, Object> map, String endpoint, String field) {
        List<ModelJsonKey> keys = Arrays.stream(ModelJsonKey.values())
                .filter(key -> key.getFied().equals(field) && matchPath(key.getEndpoint(), endpoint))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(keys)) {
            return null;
        }
        List<ModelJsonKey> invalidKeys = keys.stream()
                .filter(key -> map.containsKey(key.getCode()) && !map.get(key.getCode()).getClass().equals(key.getType()))
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(invalidKeys)) {
            StringBuilder sb = new StringBuilder(field).append(" contains the following non-compliant content:\r\n");
            invalidKeys.forEach(key -> sb.append(key.getCode())
                    .append(" value type is: ")
                    .append(key.getType().getSimpleName())
                    .append(", "));
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return null;
    }
}
