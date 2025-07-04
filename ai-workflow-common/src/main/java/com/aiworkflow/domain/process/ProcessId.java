package com.aiworkflow.domain.process;

import com.aiworkflow.domain.shared.ValueObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * 流程ID值对象
 */
public final class ProcessId extends ValueObject {
    
    private final String value;
    
    private ProcessId(String value) {
        this.value = value;
        validate();
    }
    
    public static ProcessId of(String value) {
        return new ProcessId(value);
    }
    
    public static ProcessId generate() {
        return new ProcessId("process_" + UUID.randomUUID().toString().replace("-", ""));
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    protected void validate() {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Process ID cannot be null or empty");
        }
        if (value.length() > 64) {
            throw new IllegalArgumentException("Process ID cannot exceed 64 characters");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessId processId = (ProcessId) o;
        return Objects.equals(value, processId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}