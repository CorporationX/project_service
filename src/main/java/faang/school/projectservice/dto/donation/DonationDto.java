package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
public class DonationDto {

    private Long id;

    @Positive
    private Long paymentNumber;

    @NotNull(message = "Сумма пожертвования не может быть null")
    @Positive(message = "Сумма пожертвования должна быть положительным числом")
    private BigDecimal amount;

    @NotNull(message = "Дата пожертвования не может быть null")
    @PastOrPresent(message = "Дата пожертвования должна быть в прошлом или настоящем")
    private LocalDateTime donationDate;

    @NotNull(message = "ID кампании не может быть null")
    private Long campaignId;

    @NotNull(message = "Валюта не может быть null")
    private Currency currency;

    @NotNull(message = "ID пользователя не может быть null")
    private Long userId;
}
