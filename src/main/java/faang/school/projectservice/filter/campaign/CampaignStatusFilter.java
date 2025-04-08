package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@ToString
@Component
public class CampaignStatusFilter implements CampaignFilter {

    @Override
    public boolean isApplicable(CampaignFilterDto campaignFilterDto) {
        return campaignFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignFilterDto campaignFilterDto) {
        return campaigns
                .filter(campaign -> campaign.getStatus().equals(campaignFilterDto.getStatus()));
    }
}
