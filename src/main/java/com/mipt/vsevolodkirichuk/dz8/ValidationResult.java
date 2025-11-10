package com.mipt.vsevolodkirichuk.dz8;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private boolean isValid;
    private List<String> errors;

    public ValidationResult() {
        this.isValid = true;
        this.errors = new ArrayList<>();
    }

    public ValidationResult(boolean isValid, List<String> errors) {
        this.isValid = isValid;
        this.errors = new ArrayList<>(errors);
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void addError(String error) {
        this.errors.add(error);
        this.isValid = false;
    }

    public void addErrors(List<String> errors) {
        this.errors.addAll(errors);
        if (!errors.isEmpty()) {
            this.isValid = false;
        }
    }

    @Override
    public String toString() {
        if (isValid) {
            return "ValidationResult{valid=true, errors=[]}";
        }
        return "ValidationResult{valid=false, errors=" + errors + "}";
    }
}
