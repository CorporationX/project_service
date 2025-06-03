package faang.school.projectservice.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorResponseDto {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}

