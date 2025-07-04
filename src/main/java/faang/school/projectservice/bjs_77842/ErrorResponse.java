package faang.school.projectservice.bjs_77842;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    @JsonFormat
    private LocalDateTime timestamp;

    private String message;
    private String url;
    private int status;
    private String error;

    public ErrorResponse(String message) {
        this.message = message;
    }
}