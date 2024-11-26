package faang.school.projectservice.exception;

public enum RoleProcessingExceptionMessage {
    ABSENT_INTERN_ROLE_EXCEPTION("Intern must have role INTERN.");

    private final String message;

    RoleProcessingExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}