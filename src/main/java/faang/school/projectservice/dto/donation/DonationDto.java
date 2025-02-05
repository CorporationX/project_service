package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DonationDto(
        @NotNull(message = "Номер платежа не должен быть пустым")
        Long paymentNumber,

        @Positive(message = "Сумма должна быть положительной")
        BigDecimal amount,

        @NotNull(message = "ID кампании не должен быть пустым")
        Long campaignId,

        @NotNull(message = "Валюта не должна быть пустой")
        Currency currency,

        @NotNull(message = "ID пользователя не должен быть пустым")
        Long userId
) {

}
