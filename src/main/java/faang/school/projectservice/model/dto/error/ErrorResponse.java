package faang.school.projectservice.model.dto.error;

public record ErrorResponse(
        int code,
        String message
) {
}
