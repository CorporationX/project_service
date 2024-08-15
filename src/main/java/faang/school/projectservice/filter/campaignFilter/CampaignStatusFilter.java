package faang.school.projectservice.filter.campaignFilter;

import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class CampaignStatusFilter implements CampaignFilter {

    @Override
    public boolean isApplicable(CampaignFiltersDto campaignFiltersDto) {
        return campaignFiltersDto.getStatus() != null;
    }

    @Override
    public Stream<Campaign> apply(List<Campaign> campaigns, CampaignFiltersDto campaignFiltersDto) {
        return campaigns.stream().filter(campaign -> campaign.getStatus().equals(campaignFiltersDto.getStatus()));
    }
}
