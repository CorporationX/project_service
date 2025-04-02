package faang.school.projectservice.exception.payment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentExceptionMessage {

    private static final String FAILED = "Payment is failed - %s";

    public static String getFailed(String message) {
        return String.format(FAILED, message);
    }
}
