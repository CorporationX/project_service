package faang.school.projectservice.handler.presentation;

import org.springframework.stereotype.Service;

@Service
public class ErrorMessageService {
    public ErrorResponse buildErrorResponse(
            String header,
            String message,
            String details) {
        return new ErrorResponse(header, message, details);
    }
}
