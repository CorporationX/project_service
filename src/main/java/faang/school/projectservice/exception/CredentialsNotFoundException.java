package faang.school.projectservice.exception;

public class CredentialsNotFoundException extends RuntimeException {
    public CredentialsNotFoundException(String message) {
        super(message);
    }
}
