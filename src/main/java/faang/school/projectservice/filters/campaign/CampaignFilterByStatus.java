package faang.school.projectservice.filters.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignFilterByStatus implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFilterDto filterDto) {
        return filterDto.getCampaignStatus() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto campaignFilterDto) {
        return campaignStream
                .filter(campaign -> campaign.getStatus().equals(campaignFilterDto.getCampaignStatus()));
    }
}
