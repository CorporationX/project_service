package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.payment.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DonationDto {
    @NotNull(message = "Номер платежа не может быть null")
    private Long paymentNumber;
    @NotNull(message = "Сумма доната не может быть null")
    @Min(value = 1, message = "Сумма доната не может быть меньше 1")
    private BigDecimal amount;
    @NotNull(message = "Кампания не может быть null")
    private Long campaignId;
    @NotNull(message = "Валюта не может быть null")
    private Currency currency;
    @NotNull(message = "Юзер доната не может быть null")
    private Long userId;
}
