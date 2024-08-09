package faang.school.projectservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorMessage {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm: ss")
    private LocalDateTime timestamp;

    private String message;
    private int statusCode;
    private String description;

    public ErrorMessage(String message) {
        this.message = message;
    }
}
