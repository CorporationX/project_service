package faang.school.projectservice.exeption;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends ApiException {

    public ServiceUnavailableException(String message) {
        super(message, message);
    }

    public ServiceUnavailableException(String message, String debugMessage) {
        super(message, debugMessage);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}