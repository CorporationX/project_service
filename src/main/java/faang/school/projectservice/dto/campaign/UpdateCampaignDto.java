package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateCampaignDto(
        String title,
        String description,
        BigDecimal goal,
        CampaignStatus status
) {
}