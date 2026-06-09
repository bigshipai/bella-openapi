package com.ke.bella.openapi.gateway.controller;

import com.ke.bella.openapi.common.context.OneTokenContext;
import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.dto.JsonSchema;
import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.common.annotation.OneTokenAPI;
import com.ke.bella.openapi.common.constant.EntityConstants;
import com.ke.bella.openapi.common.exception.BizParamCheckException;
import com.ke.bella.openapi.modules.console.MetadataValidator;
import com.ke.bella.openapi.db.repo.Page;
import com.ke.bella.openapi.modules.endpoint.Condition;
import com.ke.bella.openapi.modules.endpoint.EndpointCategoryTree;
import com.ke.bella.openapi.modules.endpoint.EndpointDetails;
import com.ke.bella.openapi.modules.MetaDataOps;
import com.ke.bella.openapi.modules.model.Model;
import com.ke.bella.openapi.protocol.tts.VoiceProperties;
import com.ke.bella.openapi.modules.category.CategoryService;
import com.ke.bella.openapi.modules.channel.ChannelService;
import com.ke.bella.openapi.modules.endpoint.EndpointService;
import com.ke.bella.openapi.modules.model.ModelService;
import com.ke.bella.openapi.generated.tables.pojos.CategoryDB;
import com.ke.bella.openapi.generated.tables.pojos.ChannelDB;
import com.ke.bella.openapi.generated.tables.pojos.EndpointDB;
import com.ke.bella.openapi.generated.tables.pojos.ModelDB;
import com.ke.bella.openapi.utils.JacksonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@OneTokenAPI
@RestController
@RequestMapping("/v1/meta")
@Tag(name = "Information Query")
public class MetadataController {
	@Autowired
	private EndpointService endpointService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/endpoint/details")
	public EndpointDetails listEndpointDetails(Condition.EndpointDetailsCondition condition) {
		log.info("/v1/meta/endpoint/details=== request={}", condition);
		Assert.notNull(condition.getEndpoint(), "Endpoint cannot be empty");
		String identity = OneTokenContext.getOperatorIgnoreNull() != null ? OneTokenContext.getOperator().getUserId().toString()
			: EndpointContext.getApikey().getOwnerCode();
		EndpointDetails result = endpointService.getEndpointDetails(condition, identity);
		log.info("/v1/meta/endpoint/details=== response={}", result);
		return result;
	}

	@GetMapping("/endpoint/list")
	public List<EndpointDB> listEndpoint(Condition.EndpointCondition condition) {
		log.info("/v1/meta/endpoint/list=== request={}", condition);
		List<EndpointDB> result = endpointService.listByCondition(condition);
		log.info("/v1/meta/endpoint/list=== response={}", result);
		return result;
	}

	@GetMapping("/endpoint/page")
	public Page<EndpointDB> pageEndpoint(Condition.EndpointCondition condition) {
		log.info("/v1/meta/endpoint/page=== request={}", condition);
		Page<EndpointDB> result = endpointService.pageByCondition(condition);
		log.info("/v1/meta/endpoint/page=== response={}", result);
		return result;
	}

	@GetMapping("/endpoint/info/{code}")
	public EndpointDB getEndpoint(@PathVariable String code) {
		log.info("/v1/meta/endpoint/info/{}=== request code={}", code, code);
		EndpointDB result = endpointService.getOne(EndpointService.UniqueKeyQuery.builder().endpointCode(code).build());
		log.info("/v1/meta/endpoint/info/{}=== response={}", code, result);
		return result;
	}

	@GetMapping("/model/list/for-selection")
	public List<Model> listModelForSelection(Condition.ModelCondition condition) {
		log.info("/v1/meta/model/list/for-selection=== request={}", condition);
		List<Model> result = modelService.listByConditionForSelectList(condition);
		log.info("/v1/meta/model/list/for-selection=== response={}", result);
		return result;
	}

	@GetMapping("/model/list")
	public List<Model> listModel(Condition.ModelCondition condition) {
		log.info("/v1/meta/model/list=== request={}", condition);
		List<Model> result = modelService.listByConditionWithPermission(condition, true);
		log.info("/v1/meta/model/list=== response={}", result);
		return result;
	}

	@GetMapping("/model/endpoints")
	public List<String> getModelEndpoints(@RequestParam String modelName) {
		log.info("/v1/meta/model/endpoints=== request modelName={}", modelName);
		List<String> result = modelService.getAllEndpoints(modelName);
		log.info("/v1/meta/model/endpoints=== response={}", result);
		return result;
	}

	@GetMapping("/model/page")
	public Page<ModelDB> pageModel(Condition.ModelCondition condition) {
		log.info("/v1/meta/model/page=== request={}", condition);
		Page<ModelDB> result = modelService.pageByConditionWithPermission(condition, true);
		log.info("/v1/meta/model/page=== response={}", result);
		return result;
	}

	@GetMapping("/model/info/{name}")
	public Model getModel(@PathVariable String name) {
		log.info("/v1/meta/model/info/{}=== request name={}", name, name);
		Model result = modelService.getInfo(name);
		log.info("/v1/meta/model/info/{}=== response={}", name, result);
		return result;
	}

	@GetMapping("/channel/list")
	public List<ChannelDB> listChannel(Condition.ChannelCondition condition) {
		log.info("/v1/meta/channel/list=== request={}", condition);
		fillChannelCondition(condition);
		List<ChannelDB> result = channelService.listByCondition(condition);
		log.info("/v1/meta/channel/list=== response={}", result);
		return result;
	}

	@GetMapping("/channel/page")
	public Page<ChannelDB> pageChannel(Condition.ChannelCondition condition) {
		log.info("/v1/meta/channel/page=== request={}", condition);
		fillChannelCondition(condition);
		Page<ChannelDB> result = channelService.pageByCondition(condition);
		log.info("/v1/meta/channel/page=== response={}", result);
		return result;
	}

	private void fillChannelCondition(Condition.ChannelCondition condition) {
		Operator operator = OneTokenContext.getOperatorIgnoreNull();
		String accountType = operator != null ? EntityConstants.PERSON : OneTokenContext.getApikey().getOwnerType();
		String accountCode = operator != null ? operator.getUserId().toString() : OneTokenContext.getApikey().getOwnerCode();
		condition.setVisibility(EntityConstants.PRIVATE);
		condition.setOwnerType(accountType);
		condition.setOwnerCode(accountCode);
	}

	@GetMapping("/category/list")
	public List<CategoryDB> listCategory(Condition.CategoryCondition condition) {
		log.info("/v1/meta/category/list=== request={}", condition);
		List<CategoryDB> result = categoryService.listByCondition(condition);
		log.info("/v1/meta/category/list=== response={}", result);
		return result;
	}

	@GetMapping("/category/page")
	public Page<CategoryDB> pageCategory(Condition.CategoryCondition condition) {
		log.info("/v1/meta/category/page=== request={}", condition);
		Page<CategoryDB> result = categoryService.pageByCondition(condition);
		log.info("/v1/meta/category/page=== response={}", result);
		return result;
	}

	@GetMapping("/category/tree")
	public EndpointCategoryTree listTree(Condition.CategoryTreeCondition condition) {
		log.info("/v1/meta/category/tree=== request={}", condition);
		EndpointCategoryTree categoryTree = categoryService.listTree(condition);
		log.info("/v1/meta/category/tree=== response={}", categoryTree);
		return categoryTree;
	}

	@GetMapping("/category/tree/all")
	public List<EndpointCategoryTree> listAllTree() {
		log.info("/v1/meta/category/tree/all=== request");
		List<EndpointCategoryTree> result = categoryService.listAllTree();
		log.info("/v1/meta/category/tree/all=== response={}", result);
		return result;
	}

	@GetMapping("/supplier/list")
	public List<String> listSuppliers() {
		log.info("/v1/meta/supplier/list=== request");
		List<String> result = channelService.listSuppliers();
		log.info("/v1/meta/supplier/list=== response={}", result);
		return result;
	}

	@GetMapping("/schema/modelProperty")
	public JsonSchema getModelPropertySchema(@RequestParam Set<String> endpoints) {
		log.info("/v1/meta/schema/modelProperty=== request endpoints={}", endpoints);
		JsonSchema result = endpointService.getModelPropertySchema(endpoints);
		log.info("/v1/meta/schema/modelProperty=== response={}", result);
		return result;
	}

	@GetMapping("/schema/modelFeature")
	public JsonSchema getModelFeatureSchema(@RequestParam Set<String> endpoints) {
		log.info("/v1/meta/schema/modelFeature=== request endpoints={}", endpoints);
		JsonSchema result = endpointService.getModelFeatureSchema(endpoints);
		log.info("/v1/meta/schema/modelFeature=== response={}", result);
		return result;
	}

	@GetMapping("/schema/priceInfo")
	public JsonSchema getPriceInfoSchema(@RequestParam String entityType, @RequestParam String entityCode) {
		log.info("/v1/meta/schema/priceInfo=== request entityType={}, entityCode={}", entityType, entityCode);
		JsonSchema result = endpointService.getPriceInfoSchema(entityType, entityCode);
		log.info("/v1/meta/schema/priceInfo=== response={}", result);
		return result;
	}

	@GetMapping("/schema/channelInfo")
	public JsonSchema getChannelInfoSchema(@RequestParam String entityType, @RequestParam String entityCode, @RequestParam String protocol) {
		log.info("/v1/meta/schema/channelInfo=== request entityType={}, entityCode={}, protocol={}", entityType, entityCode, protocol);
		JsonSchema result = endpointService.getChannelInfo(entityType, entityCode, protocol);
		log.info("/v1/meta/schema/channelInfo=== response={}", result);
		return result;
	}

	@GetMapping("/protocol/list")
	public Map<String, String> listProtocols(@RequestParam String entityType, @RequestParam String entityCode) {
		log.info("/v1/meta/protocol/list=== request entityType={}, entityCode={}", entityType, entityCode);
		Map<String, String> result = endpointService.listProtocols(entityType, entityCode);
		log.info("/v1/meta/protocol/list=== response={}", result);
		return result;
	}

	@PostMapping("/channel/private")
	public ChannelDB createPrivateChannel(@RequestBody MetaDataOps.ChannelCreateOp op) {
		log.info("/v1/meta/channel/private POST=== request={}", op);
		op.setVisibility(EntityConstants.PRIVATE);
		Operator operator = OneTokenContext.getOperatorIgnoreNull();
		if (StringUtils.isBlank(op.getOwnerType()) || StringUtils.isBlank(op.getOwnerCode())) {
			String accountType = operator != null ? EntityConstants.PERSON : OneTokenContext.getApikey().getOwnerType();
			String accountCode = operator != null ? operator.getUserId().toString() : OneTokenContext.getApikey().getOwnerCode();
			String accountName = operator != null ? operator.getUserName() : OneTokenContext.getApikey().getOwnerName();
			op.setOwnerType(accountType);
			op.setOwnerCode(accountCode);
			if (StringUtils.isNotBlank(accountName)) {
				op.setOwnerName(accountName);
			}
		}
		MetadataValidator.checkChannelCreateOp(op);
		ChannelDB result = channelService.createChannel(op);
		log.info("/v1/meta/channel/private POST=== response={}", result);
		return result;
	}

	@PutMapping("/channel/private")
	public Boolean updatePrivateChannel(@RequestBody MetaDataOps.ChannelUpdateOp op) {
		log.info("/v1/meta/channel/private PUT=== request={}", op);
		checkChannel(op.getChannelCode());
		channelService.updateChannel(op);
		Boolean result = true;
		log.info("/v1/meta/channel/private PUT=== response={}", result);
		return result;
	}

	@PostMapping("/channel/private/activate")
	public Boolean activatePrivateChannel(@RequestBody MetaDataOps.ChannelStatusOp op) {
		log.info("/v1/meta/channel/private/activate=== request={}", op);
		checkChannel(op.getChannelCode());
		channelService.changeStatus(op.getChannelCode(), true);
		Boolean result = true;
		log.info("/v1/meta/channel/private/activate=== response={}", result);
		return result;
	}

	@PostMapping("/channel/private/inactivate")
	public Boolean inactivatePrivateChannel(@RequestBody MetaDataOps.ChannelStatusOp op) {
		log.info("/v1/meta/channel/private/inactivate=== request={}", op);
		checkChannel(op.getChannelCode());
		channelService.changeStatus(op.getChannelCode(), false);
		Boolean result = true;
		log.info("/v1/meta/channel/private/inactivate=== response={}", result);
		return result;
	}

	@GetMapping("/property/voice")
	public VoiceProperties fetchVoiceProperty(Condition.ChannelCondition condition) {
		log.info("/v1/meta/property/voice=== request={}", condition);
		String model = condition.getEntityCode();
		if (EntityConstants.ENDPOINT.equals(condition.getEntityType())) {
			model = null;
			List<ChannelDB> channels = channelService.listActives(condition.getEntityType(), condition.getEntityCode());
			if (CollectionUtils.isEmpty(channels)) {
				log.info("/v1/meta/property/voice=== response=null");
				return null;
			}
			Map<String, Object> channelInfos = JacksonUtils.toMap(channels.get(0).getChannelInfo());
			if (MapUtils.isNotEmpty(channelInfos) && channelInfos.containsKey("modelName")) {
				model = channelInfos.get("modelName").toString();
			}
		}
		if (model == null) {
			log.info("/v1/meta/property/voice=== response=null");
			return null;
		}
		ModelDB modelDB = modelService.getOne(model);
		if (modelDB == null) {
			log.info("/v1/meta/property/voice=== response=null");
			return null;
		}
		VoiceProperties result = JacksonUtils.deserialize(modelDB.getProperties(), VoiceProperties.class);
		log.info("/v1/meta/property/voice=== response={}", result);
		return result;
	}

	private void checkChannel(String channelCode) {
		ChannelDB channel = channelService.getOne(channelCode);
		if (channel == null) {
			throw new BizParamCheckException("Channel does not exist");
		}
		if (EntityConstants.PUBLIC.equals(channel.getVisibility())) {
			throw new BizParamCheckException("Only private channels can be modified");
		}

		// 检查用户是否有权限更新该渠道
		Operator operator = OneTokenContext.getOperatorIgnoreNull();
		String accountType = operator != null ? EntityConstants.PERSON : OneTokenContext.getApikey().getOwnerType();
		String accountCode = operator != null ? operator.getUserId().toString() : OneTokenContext.getApikey().getOwnerCode();

		if (!channel.getOwnerType().equals(accountType) || !channel.getOwnerCode().equals(accountCode)) {
			throw new BizParamCheckException("Can only modify your own private channels");
		}
	}
}
