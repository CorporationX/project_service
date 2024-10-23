package faang.school.projectservice.model.dto.campaign;

import faang.school.projectservice.model.dto.client.Currency;
import faang.school.projectservice.model.entity.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CampaignDto(
        @NotNull
        @Positive
        Long projectId,
        @NotNull
        @Size(min = 1, max = 256)
        String title,
        String description,
        BigDecimal amountRaised,
        @NotNull
        CampaignStatus status,
        @NotNull
        Currency currency
) {
}
