package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

@Component
public class CampaignCreationDateFilterStrategy implements CampaignFilterStrategy {
    @Override
    public boolean filter(Campaign campaign, CampaignFilterDto campaignFilterDto) {
        return campaign.getCreatedAt().toLocalDate().equals(campaignFilterDto.getCreatedAt());
    }
}
