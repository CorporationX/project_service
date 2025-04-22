package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.PaymentRequestDto;
import faang.school.projectservice.dto.client.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${services.payment-service.host}:${services.payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponseDto sendPayment(@RequestBody PaymentRequestDto paymentRequestDto);
}
