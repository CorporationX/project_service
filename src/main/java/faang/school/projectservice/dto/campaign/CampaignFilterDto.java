package faang.school.projectservice.dto.campaign;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import faang.school.projectservice.model.CampaignStatus;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CampaignFilterDto(String startDate, CampaignStatus status, Long createdId) {
}
