package com.aiworkflow.engine.core;

/**
 * 流程引擎异常类
 */
public class ProcessEngineException extends RuntimeException {

    private final String errorCode;

    public ProcessEngineException(String message) {
        super(message);
        this.errorCode = "PROCESS_ENGINE_ERROR";
    }

    public ProcessEngineException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PROCESS_ENGINE_ERROR";
    }

    public ProcessEngineException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProcessEngineException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 流程定义异常
     */
    public static class ProcessDefinitionException extends ProcessEngineException {
        public ProcessDefinitionException(String message) {
            super("PROCESS_DEFINITION_ERROR", message);
        }

        public ProcessDefinitionException(String message, Throwable cause) {
            super("PROCESS_DEFINITION_ERROR", message, cause);
        }
    }

    /**
     * 流程执行异常
     */
    public static class ProcessExecutionException extends ProcessEngineException {
        public ProcessExecutionException(String message) {
            super("PROCESS_EXECUTION_ERROR", message);
        }

        public ProcessExecutionException(String message, Throwable cause) {
            super("PROCESS_EXECUTION_ERROR", message, cause);
        }
    }

    /**
     * 任务执行异常
     */
    public static class TaskExecutionException extends ProcessEngineException {
        public TaskExecutionException(String message) {
            super("TASK_EXECUTION_ERROR", message);
        }

        public TaskExecutionException(String message, Throwable cause) {
            super("TASK_EXECUTION_ERROR", message, cause);
        }
    }

    /**
     * Agent异常
     */
    public static class AgentException extends ProcessEngineException {
        public AgentException(String message) {
            super("AGENT_ERROR", message);
        }

        public AgentException(String message, Throwable cause) {
            super("AGENT_ERROR", message, cause);
        }
    }

    /**
     * BPMN解析异常
     */
    public static class BpmnParseException extends ProcessEngineException {
        public BpmnParseException(String message) {
            super("BPMN_PARSE_ERROR", message);
        }

        public BpmnParseException(String message, Throwable cause) {
            super("BPMN_PARSE_ERROR", message, cause);
        }
    }
}