package faang.school.projectservice.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message, Exception eMessage) {
        super(message, eMessage);
    }

    public PaymentFailedException(String message) {
        super(message);
    }
}
