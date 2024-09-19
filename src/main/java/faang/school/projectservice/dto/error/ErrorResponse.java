package faang.school.projectservice.dto.error;

public record ErrorResponse(
        int code,
        String message
) {
}
