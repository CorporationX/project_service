package faang.school.projectservice.service.campaignfilter;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;

import java.util.stream.Stream;

public class CampaignStatusFilter implements CampaignFilter{

    @Override
    public boolean isApplicable(CampaignFilterDto filterDto) {
       return filterDto.getStatus() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto filterDto) {
        return campaignStream.filter(campaign -> campaign.getStatus() == filterDto.getStatus());
    }
}
