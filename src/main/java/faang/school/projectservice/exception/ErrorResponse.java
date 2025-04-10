package faang.school.projectservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String report;
    private String message;
    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();
}
