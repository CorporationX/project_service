package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.filter.donation.Value;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO-класс с критериями для фильтрации донатов.
 * <p>
 * Используется для фильтрации донатов
 * </p>
 * <p>
 * Содержит следующие поля:
 * <ul>
 *     <li>{@link #donationTime Время создания доната}</li>
 *     <li>{@link #currency Валюта}</li>
 *     <li>{@link #value MIN - для поиска минимального доната, MAX - для максимального}</li>
 * </ul>
 * </p>
 *
 * @author juzu400
 */
@Data
public class DonationFilterDto {
    private LocalDateTime donationTime;
    private Currency currency;
    private Value value;
}
