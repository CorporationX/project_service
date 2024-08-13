package faang.school.projectservice.filter.campaignFilter;

import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface CampaignFilter {
    boolean isApplicable(CampaignFiltersDto campaignFiltersDto);

    Stream<Campaign> apply(List<Campaign> campaigns, CampaignFiltersDto campaignFiltersDto);
}
