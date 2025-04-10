package faang.school.projectservice.dto.error;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String statusCode,
        String message
) {
}
