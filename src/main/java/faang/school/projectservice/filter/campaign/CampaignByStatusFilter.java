package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignByStatusFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignDto campaignDto) {
        return campaignDto.getStatus() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignDto campaignDto) {
        return campaigns
                .filter(campaign -> campaign.getStatus().equals(campaignDto.getStatus()));
    }
}
