package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;

public interface CampaignFilterStrategy {
    boolean filter(Campaign campaign, CampaignFilterDto campaignFilterDto);
    boolean isApplicable(CampaignFilterDto campaignFilterDto);
}
