package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;

import java.util.stream.Stream;

public interface CampaignFilter {

    boolean isApplicable(CampaignDto campaignDto);

    Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignDto campaignDto);
}
