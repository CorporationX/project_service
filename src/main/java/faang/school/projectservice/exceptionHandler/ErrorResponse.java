package faang.school.projectservice.exceptionHandler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final String nameError;
    private LocalDateTime localDateTime = LocalDateTime.now();
}
