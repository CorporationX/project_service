package faang.school.projectservice.client;

import faang.school.projectservice.dto.donation.ExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fixerClient", url = "https://data.fixer.io/api")
public interface FixerClient {

    @GetMapping("/latest")
    ExchangeRateResponse getExchangeRates(
            @RequestParam("access_key") String accessKey,
            @RequestParam("symbols") String symbols // Убрали параметр base
    );
}
