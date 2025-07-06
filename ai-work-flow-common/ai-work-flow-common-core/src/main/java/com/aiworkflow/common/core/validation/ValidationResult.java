package com.aiworkflow.common.core.validation;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果
 */
@Data
public class ValidationResult {
    
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;
    
    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public String getErrorMessage() {
        return String.join("\n", this.errors);
    }
}