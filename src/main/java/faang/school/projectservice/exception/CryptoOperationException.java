package faang.school.projectservice.exception;

public class CryptoOperationException extends RuntimeException {
    public CryptoOperationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
