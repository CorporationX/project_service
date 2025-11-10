package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FieldNameConstants
@Builder
public record CampaignDto(
        Long id,
        String title,
        String description,
        BigDecimal goal,
        BigDecimal amountRaised,
        CampaignStatus status,
        Long projectId,
        Currency currency,
        LocalDateTime createdAt,
        Long createdBy
) {
}