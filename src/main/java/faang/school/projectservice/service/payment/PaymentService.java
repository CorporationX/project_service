package faang.school.projectservice.service.payment;

import faang.school.projectservice.client.feign.PaymentServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.exception.payment.PaymentFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    public PaymentResponse makePayment(BigDecimal amount, Currency currency) {
        log.info("Initiating payment for amount {} {}", amount, currency);

        long paymentNumber = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, amount, currency);

        ResponseEntity<PaymentResponse> paymentResponse = paymentServiceClient.sendPayment(paymentRequest);
        log.debug("Received payment response: {}", paymentResponse);

        if (paymentResponse.getStatusCode() != HttpStatus.OK || paymentResponse.getBody() == null) {
            log.error("Payment failed. Status: {}, Response Body: {}",
                    paymentResponse.getStatusCode(),
                    paymentResponse.getBody());
            throw new PaymentFailedException("Payment failed for amount " + amount + " " + currency);
        }

        log.info("Payment processed successfully. Payment number: {}", paymentNumber);
        return paymentResponse.getBody();
    }
}
