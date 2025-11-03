package faang.school.projectservice.exception;

import lombok.Getter;

@Getter
public class ExternalServiceConnectException extends RuntimeException {
    private final String serviceName;

    public ExternalServiceConnectException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public ExternalServiceConnectException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }
}