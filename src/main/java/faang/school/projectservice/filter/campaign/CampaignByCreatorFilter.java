package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignByCreatorFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignDto campaignDto) {
        return campaignDto.getCreatedBy() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignDto campaignDto) {
        Long creatorId = campaignDto.getCreatedBy();
        return campaigns
                .filter(campaign -> campaign.getCreatedBy().equals(creatorId));
    }
}
