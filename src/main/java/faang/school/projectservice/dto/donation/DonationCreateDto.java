package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "DTO-класс для создания доната.")
@Data
public class DonationCreateDto {
    @NotNull(message = "paymentNumber cannot be null")
    @Schema(description = "Номер платежа", example = "12345")
    private Long paymentNumber;

    @NotNull(message = "amount cannot be null")
    @Schema(description = "Сумма доната", example = "100.00")
    private BigDecimal amount;

    @NotNull(message = "campaignId cannot be null")
    @Schema(description = "Идентификатор компании, которая получает донат", example = "1")
    private Long campaignId;

    @NotNull(message = "currency cannot be null")
    @Schema(description = "Валюта доната", allowableValues = {"USD", "EUR"})
    private Currency currency;
}
