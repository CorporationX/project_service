package faang.school.projectservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponseDto {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}

