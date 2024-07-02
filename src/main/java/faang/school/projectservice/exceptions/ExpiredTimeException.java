package faang.school.projectservice.exceptions;

public class ExpiredTimeException extends RuntimeException {
    public ExpiredTimeException(String message) {
        super(message);
    }
}
