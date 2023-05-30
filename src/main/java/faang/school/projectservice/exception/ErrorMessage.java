package faang.school.projectservice.exception;

public enum ErrorMessage {

    PROJECT_ALREADY_EXISTS("A project with the name %s already exists for this user"),
    FILE_EXCEPTION("Error with reading file %s"),
    FILE_SIZE_EXCEEDED("File size exceeded the limit of %s MB")
    ;
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
