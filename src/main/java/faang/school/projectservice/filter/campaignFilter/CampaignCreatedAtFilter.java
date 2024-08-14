package faang.school.projectservice.filter.campaignFilter;

import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.model.Campaign;

import java.util.List;
import java.util.stream.Stream;

public class CampaignCreatedAtFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFiltersDto campaignFiltersDto) {
        return campaignFiltersDto.getCreatedAt() != null;
    }

    @Override
    public Stream<Campaign> apply(List<Campaign> campaigns, CampaignFiltersDto campaignFiltersDto) {
        return campaigns.stream().filter(campaign -> campaign.getCreatedAt().isAfter(campaignFiltersDto.getCreatedAt()));
    }
}
