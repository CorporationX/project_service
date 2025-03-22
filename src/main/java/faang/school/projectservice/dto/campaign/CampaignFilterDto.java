package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;

public record CampaignFilterDto(String startDate, CampaignStatus status, Long createdId) {
}
