package faang.school.projectservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String url;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}