package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO-класс для предоставления информации о существующем донате.
 * <p>
 * Используется для передачи данных о донате между слоями приложения.
 * </p>
 * <p>
 * Содержит следующие поля:
 * <ul>
 *     <li>{@link #id Идентификатор доната}</li>
 *     <li>{@link #paymentNumber Номер платежа}</li>
 *     <li>{@link #amount Сумма платежа}</li>
 *     <li>{@link #donationTime Время создания доната}</li>
 *     <li>{@link #campaignId Идентификатор компании, которая получает донат}</li>
 *     <li>{@link #currency Валюта}</li>
 *     <li>{@link #userId Идентификатор пользователя, который совершает донат}</li>
 * </ul>
 * </p>
 *
 * @author juzu400
 */
@Data
public class DonationViewDto {
    @NotNull(message = "id cannot be null")
    private Long id;

    private Long paymentNumber;

    @NotNull(message = "amount cannot be null")
    private BigDecimal amount;

    private LocalDateTime donationTime;

    @NotNull(message = "campaignId cannot be null")
    private Long campaignId;

    private Currency currency;

    @NotNull(message = "userId cannot be null")
    private Long userId;
}
