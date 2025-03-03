package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-service",
        url = "${services.payment-service.url}"
)
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse sendPayment(@RequestBody PaymentRequest paymentRequest);
}
