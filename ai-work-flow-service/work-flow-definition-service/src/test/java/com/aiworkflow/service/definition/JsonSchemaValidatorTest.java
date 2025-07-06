package com.aiworkflow.service.definition;

import com.aiworkflow.common.core.util.JsonSchemaValidator;
import com.aiworkflow.common.core.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaValidator 简单测试
 */
class JsonSchemaValidatorTest {

    private String simpleSchema;

    @BeforeEach
    void setUp() {
        // 简单的Schema定义
        simpleSchema = """
        {
          "type": "object",
          "required": ["model", "prompt"],
          "properties": {
            "model": {
              "type": "string",
              "enum": ["gpt-4", "claude-3"]
            },
            "prompt": {
              "type": "string",
              "minLength": 1
            },
            "temperature": {
              "type": "number",
              "minimum": 0,
              "maximum": 2
            }
          }
        }
        """;
        
        JsonSchemaValidator.clearCache();
    }

    @Test
    void testValidConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("model", "gpt-4");
        config.put("prompt", "测试提示");
        config.put("temperature", 0.7);

        boolean result = JsonSchemaValidator.validate(simpleSchema, config);
        assertTrue(result);
    }

    @Test
    void testInvalidConfig_MissingRequired() {
        Map<String, Object> config = new HashMap<>();
        config.put("model", "gpt-4");
        // 缺少 prompt

        boolean result = JsonSchemaValidator.validate(simpleSchema, config);
        assertFalse(result);
    }

    @Test
    void testInvalidConfig_WrongType() {
        Map<String, Object> config = new HashMap<>();
        config.put("model", "gpt-4");
        config.put("prompt", "测试");
        config.put("temperature", "not_a_number"); // 错误类型

        boolean result = JsonSchemaValidator.validate(simpleSchema, config);
        assertFalse(result);
    }

    @Test
    void testValidateDetailed() {
        Map<String, Object> config = new HashMap<>();
        config.put("model", "invalid_model"); // 不在枚举中
        config.put("temperature", 5.0); // 超出范围

        ValidationResult result = JsonSchemaValidator.validateDetailed(simpleSchema, config);

        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void testNodeConfigValidation() {
        Map<String, Object> config = new HashMap<>();
        config.put("model", "gpt-4");
        config.put("prompt", "测试");

        ValidationResult result = JsonSchemaValidator.validateNodeConfig(simpleSchema, config);
        assertTrue(result.isValid());
    }

    @Test
    void testNullConfig() {
        ValidationResult result = JsonSchemaValidator.validateNodeConfig(simpleSchema, null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("cannot be null"));
    }

    @Test
    void testCache() {
        Map<String, Object> config = new HashMap<>();
        config.put("model", "gpt-4");
        config.put("prompt", "测试");

        assertEquals(0, JsonSchemaValidator.getCacheSize());
        
        JsonSchemaValidator.validate(simpleSchema, config);
        assertEquals(1, JsonSchemaValidator.getCacheSize());
        
        JsonSchemaValidator.clearCache();
        assertEquals(0, JsonSchemaValidator.getCacheSize());
    }
}