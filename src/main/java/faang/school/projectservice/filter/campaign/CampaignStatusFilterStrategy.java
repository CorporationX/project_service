package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

@Component
public class CampaignStatusFilterStrategy implements CampaignFilterStrategy {
    @Override
    public boolean filter(Campaign campaign, CampaignFilterDto campaignFilterDto) {
        return campaign.getStatus() == campaignFilterDto.getStatus();
    }
}
