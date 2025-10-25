package faang.school.projectservice.exception.handler;

public record Violation(
        String fieldName,
        String message
) {
}