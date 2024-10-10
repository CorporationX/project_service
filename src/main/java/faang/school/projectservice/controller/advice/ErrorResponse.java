package faang.school.projectservice.controller.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {
    private final int statusCode;
    private final String message;
    private final Map<String, String> errors;
  
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();

    private ErrorResponse(HttpStatus status, String message) {
        this.statusCode = status.value();
        this.message = message;
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, message);
    }
}

