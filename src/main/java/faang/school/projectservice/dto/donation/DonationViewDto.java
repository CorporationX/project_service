package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO для представления доната")
@Data
public class DonationViewDto {
    @NotNull(message = "id cannot be null")
    @Schema(description = "ID доната", example = "1")
    private Long id;

    @NotNull(message = "paymentNumber cannot be null")
    @Schema(description = "Номер платежа", example = "12345")
    private Long paymentNumber;

    @NotNull(message = "amount cannot be null")
    @Schema(description = "Сумма доната", example = "100.00")
    private BigDecimal amount;

    @NotNull(message = "donationTime cannot be null")
    @Schema(description = "Время отправки доната", example = "2023-10-01T12:00:00")
    private LocalDateTime donationTime;

    @NotNull(message = "campaignId cannot be null")
    @Schema(description = "ID кампании", example = "1")
    private Long campaignId;

    @NotNull(message = "currency cannot be null")
    @Schema(description = "Валюта доната", example = "USD")
    private Currency currency;

    @NotNull(message = "userId cannot be null")
    @Schema(description = "ID пользователя, отправившего донат", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
}
