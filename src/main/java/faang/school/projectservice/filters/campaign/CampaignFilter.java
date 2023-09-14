package faang.school.projectservice.filters.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;

import java.util.stream.Stream;

public interface CampaignFilter {

    boolean isApplicable(CampaignFilterDto filterDto);

    Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto campaignFilterDto);
}
