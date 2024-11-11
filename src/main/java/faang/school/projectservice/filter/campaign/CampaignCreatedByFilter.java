package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignCreatedByFilter implements Filter<CampaignFilterDto, Campaign> {

    @Override
    public boolean isApplicable(CampaignFilterDto filterDto) {
        return filterDto.getCreatedBy() != null;
    }

    @Override
    public Stream<Campaign> applyFilter(Stream<Campaign> campaigns, CampaignFilterDto filterDto) {
        return campaigns.filter(campaign -> campaign.getCreatedBy() == filterDto.getCreatedBy());
    }
}
