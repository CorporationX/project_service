package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.ExchangeRateResponse;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyConverter {
    private static final String ACCESS_KEY = "63b0f844bd6b0839998f4f1bb35a3d0a";
    private final CampaignRepository campaignRepository;
    private final FixerClient fixerClient;

    public void converter(DonationDto donation) {
        Currency campaignCurrency = campaignRepository.getById(donation.getCampaignId()).getCurrency();
        Currency donationCurrency = donation.getCurrency();

        if (!campaignCurrency.equals(donationCurrency)) {
            log.info("Fetching exchange rates for: {} -> {}", campaignCurrency, donationCurrency);
            ExchangeRateResponse exchangeRate = fixerClient.getExchangeRates(
                    ACCESS_KEY, campaignCurrency.toString() + "," + donationCurrency.toString()
            );

            log.info("Received exchange rates: {}", exchangeRate);

            BigDecimal amount = donation.getAmount();


            BigDecimal campaignRate = BigDecimal.valueOf(exchangeRate.getRates().get(campaignCurrency.toString()));
            BigDecimal donationRate = BigDecimal.valueOf(exchangeRate.getRates().get(donationCurrency.toString()));

            BigDecimal convertedAmount = amount.divide(donationRate, 6, RoundingMode.HALF_UP)
                    .multiply(campaignRate)
                    .setScale(2, RoundingMode.HALF_UP);

            donation.setAmount(convertedAmount);
            donation.setCurrency(campaignCurrency);
        }
    }
}
