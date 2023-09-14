package faang.school.projectservice.filters.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignFilterByCreatedBy implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFilterDto filterDto) {
        return filterDto.getCreatedBy() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto campaignFilterDto) {
        return campaignStream
                .filter(campaign -> campaign.getCreatedBy().equals(campaignFilterDto.getCreatedBy()));
    }
}
