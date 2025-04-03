package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.filter.donation.Value;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "DTO-класс для фильтрации донатов")
@Data
public class DonationFilterDto {
    @Schema(description = "Дата создания доната", example = "2023-10-01T10:00:00")
    private LocalDateTime donationTime;

    @Schema(description = "Идентификатор компании, которая получает донат", example = "1")
    private Currency currency;

    @Schema(description = "Используется для поиска максимального или минимального доната", allowableValues = {"MIN", "MAX"})
    private Value value;
}
