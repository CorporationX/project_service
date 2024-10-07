package faang.school.projectservice.dto.error;

import lombok.Getter;

@Getter
public enum ErrorType {
    VALIDATION_ERROR("Validation error"),
    NOT_FOUND("Requested entity Not Found"),
    ILLEGAL_STATE("An unexpected error occurred"),
    EXTERNAL_SERVICE_ERROR("Error interacting with external service");

    private String message;

    ErrorType(String message) {
        this.message = message;
    }
}
