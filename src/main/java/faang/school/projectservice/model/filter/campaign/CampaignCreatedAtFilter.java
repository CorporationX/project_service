package faang.school.projectservice.model.filter.campaign;

import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.entity.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignCreatedAtFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFilterDto filters) {
        return filters.createdAt() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto filters) {
        return campaignStream.filter(campaign -> campaign.getCreatedAt().isEqual(filters.createdAt()));
    }
}
