package faang.school.projectservice.service.payment;

import faang.school.projectservice.client.feign.PaymentServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.PaymentStatus;
import faang.school.projectservice.exception.payment.PaymentFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    private final BigDecimal testAmount = BigDecimal.valueOf(100.50);
    private final Currency testCurrency = Currency.USD;

    @Test
    public void testMakePayment_ShouldReturnResponse_WhenPaymentIsSuccessful() {
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        PaymentResponse actualResponse = paymentService.makePayment(testAmount, testCurrency);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testMakePayment_ShouldThrowException_WhenPaymentFails() {
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

        PaymentFailedException exception = assertThrows(PaymentFailedException.class,
                () -> paymentService.makePayment(testAmount, testCurrency));

        assertEquals("Payment failed for amount 100.5 USD", exception.getMessage());
        verify(paymentServiceClient, times(1))
                .sendPayment(any(PaymentRequest.class));
    }
}
