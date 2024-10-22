package faang.school.projectservice.model.filter.campaign;

import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.entity.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignCreatedByFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFilterDto filters) {
        return filters.createdBy() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto filters) {
        return campaignStream.filter(campaign -> campaign.getCreatedBy().equals(filters.createdBy()));
    }
}
