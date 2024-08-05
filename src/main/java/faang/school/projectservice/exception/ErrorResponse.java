package faang.school.projectservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String url;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}