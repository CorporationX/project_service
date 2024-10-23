package faang.school.projectservice.model.filter.campaign;

import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.entity.Campaign;

import java.util.stream.Stream;

public interface CampaignFilter {
    boolean isApplicable(CampaignFilterDto filters);

    Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto filters);
}
