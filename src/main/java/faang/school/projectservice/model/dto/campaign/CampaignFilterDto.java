package faang.school.projectservice.model.dto.campaign;

import faang.school.projectservice.model.entity.CampaignStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CampaignFilterDto(
        LocalDateTime createdAt,
        CampaignStatus status,
        Long createdBy
) {
}
