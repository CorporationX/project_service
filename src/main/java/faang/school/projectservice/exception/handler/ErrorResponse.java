package faang.school.projectservice.exception.handler;

public record ErrorResponse(
        String description,
        String errors
) {
    ErrorResponse(String description) {
        this(description, null);
    }
}