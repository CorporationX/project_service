package faang.school.projectservice.service.campaignfilter;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;

import java.util.stream.Stream;

public class CampaignCreatedByFilter implements CampaignFilter{

    @Override
    public boolean isApplicable(CampaignFilterDto filterDto) {
        return filterDto.getCreatedBy() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto filterDto) {
        return campaignStream.filter(campaign ->
                campaign.getCreatedBy().equals(filterDto.getCreatedBy()));
    }
}
