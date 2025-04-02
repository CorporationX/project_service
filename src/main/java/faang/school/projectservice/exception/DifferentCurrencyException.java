package faang.school.projectservice.exception;

public class DifferentCurrencyException extends RuntimeException {

    public DifferentCurrencyException(String message, Object... args) {
        super(String.format(message, args));
    }
}
