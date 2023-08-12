package faang.school.projectservice.exceptionhandler.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final String message;
    private LocalDateTime localDateTime = LocalDateTime.now();
}
