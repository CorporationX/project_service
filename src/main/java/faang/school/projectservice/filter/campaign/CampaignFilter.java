package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import lombok.ToString;

import java.util.stream.Stream;

public interface CampaignFilter {
    boolean isApplicable(CampaignFilterDto campaignFilterDto);

    Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignFilterDto campaignFilterDto);
}
