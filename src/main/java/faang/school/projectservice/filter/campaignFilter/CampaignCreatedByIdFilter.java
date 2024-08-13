package faang.school.projectservice.filter.campaignFilter;

import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class CampaignCreatedByIdFilter implements CampaignFilter {

    @Override
    public boolean isApplicable(CampaignFiltersDto campaignFiltersDto) {
        return Objects.nonNull(campaignFiltersDto.getCreatedById());
    }

    @Override
    public Stream<Campaign> apply(List<Campaign> campaigns, CampaignFiltersDto campaignFiltersDto) {
        return campaigns.stream().filter(campaign -> campaign.getCreatedBy().equals(campaignFiltersDto.getCreatedById()));
    }
}
