package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignDataFilter implements CampaignFilter {

    @Override
    public boolean isApplicable(CampaignFilterDto campaignFilterDto) {
        return campaignFilterDto.getCreatedAfter() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignFilterDto campaignFilterDto) {
        return campaigns
                .filter(campaign -> campaign.getCreatedAt().isAfter(campaignFilterDto.getCreatedAfter())
                        || campaign.getCreatedBy().equals(campaignFilterDto.getCreatedBy()));
    }
}
