package com.aiworkflow.common.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.aiworkflow.common.core.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON Schema 验证工具
 */
public class JsonSchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaValidator.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    // 缓存已编译的 Schema，避免重复解析
    private static final Map<String, JsonSchema> schemaCache = new ConcurrentHashMap<>();

    /**
     * 验证 JSON 数据是否符合 Schema
     *
     * @param schemaJson Schema JSON 字符串
     * @param data 待验证的数据
     * @return 是否验证通过
     */
    public static boolean validate(String schemaJson, Map<String, Object> data) {
        try {
            JsonSchema schema = getOrCreateSchema(schemaJson);
            JsonNode dataNode = objectMapper.valueToTree(data);

            Set<ValidationMessage> errors = schema.validate(dataNode);
            return errors.isEmpty();

        } catch (Exception e) {
            log.error("JSON Schema validation failed", e);
            return false;
        }
    }

    /**
     * 详细验证，返回验证结果
     *
     * @param schemaJson Schema JSON 字符串
     * @param data 待验证的数据
     * @return 详细的验证结果
     */
    public static ValidationResult validateDetailed(String schemaJson, Map<String, Object> data) {
        ValidationResult result = new ValidationResult();

        try {
            JsonSchema schema = getOrCreateSchema(schemaJson);
            JsonNode dataNode = objectMapper.valueToTree(data);

            Set<ValidationMessage> validationMessages = schema.validate(dataNode);

            if (validationMessages.isEmpty()) {
                // 验证通过
                result.setValid(true);
            } else {
                // 验证失败，添加错误信息
                result.setValid(false);
                for (ValidationMessage message : validationMessages) {
                    result.addError(formatValidationMessage(message));
                }
            }

        } catch (Exception e) {
            log.error("JSON Schema validation failed", e);
            result.setValid(false);
            result.addError("Schema validation error: " + e.getMessage());
        }

        return result;
    }

    /**
     * 验证节点配置 - 针对任务实例的配置验证
     *
     * @param schemaJson Schema JSON 字符串
     * @param configData 配置数据 (对应 ai_task_instance.config_data)
     * @return 验证结果
     */
    public static ValidationResult validateNodeConfig(String schemaJson, Object configData) {
        ValidationResult result = new ValidationResult();

        if (configData == null) {
            result.setValid(false);
            result.addError("Configuration data cannot be null");
            return result;
        }

        try {
            JsonSchema schema = getOrCreateSchema(schemaJson);
            JsonNode dataNode;

            // 处理不同类型的配置数据
            if (configData instanceof Map) {
                dataNode = objectMapper.valueToTree(configData);
            } else if (configData instanceof String) {
                dataNode = objectMapper.readTree((String) configData);
            } else {
                dataNode = objectMapper.valueToTree(configData);
            }

            Set<ValidationMessage> validationMessages = schema.validate(dataNode);

            if (validationMessages.isEmpty()) {
                result.setValid(true);
            } else {
                result.setValid(false);
                for (ValidationMessage message : validationMessages) {
                    result.addError(formatValidationMessage(message));
                }
            }

        } catch (Exception e) {
            log.error("Node config validation failed", e);
            result.setValid(false);
            result.addError("Config validation error: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取或创建 Schema，带缓存机制
     */
    private static JsonSchema getOrCreateSchema(String schemaJson) throws Exception {
        // 使用 schema 内容的 hash 作为缓存键
        String cacheKey = String.valueOf(schemaJson.hashCode());

        return schemaCache.computeIfAbsent(cacheKey, k -> {
            try {
                JsonNode schemaNode = objectMapper.readTree(schemaJson);
                return factory.getSchema(schemaNode);
            } catch (Exception e) {
                log.error("Failed to create JSON Schema", e);
                throw new RuntimeException("Invalid JSON Schema: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 格式化验证错误信息
     */
    private static String formatValidationMessage(ValidationMessage message) {
        String path = message.getPath();
        String msg = message.getMessage();

        if (path != null && !path.isEmpty()) {
            return String.format("字段 '%s': %s", path, msg);
        } else {
            return msg;
        }
    }

    /**
     * 清空 Schema 缓存
     */
    public static void clearCache() {
        schemaCache.clear();
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return schemaCache.size();
    }
}