package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignCreatedAtFilter implements CampaignFilter {

    @Override
    public boolean isApplicable(CampaignFilterDto filtersDto) {
        return filtersDto.createdAt() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignFilterDto filtersDto) {
        return campaigns.filter(campaign -> campaign.getCreatedAt().equals(filtersDto.createdAt()));
    }
}