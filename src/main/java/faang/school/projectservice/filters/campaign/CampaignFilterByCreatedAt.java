package faang.school.projectservice.filters.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Component
public class CampaignFilterByCreatedAt implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFilterDto filterDto) {
        return filterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto campaignFilterDto) {
        return campaignStream
                .filter(campaign -> campaign
                        .getCreatedAt().truncatedTo(ChronoUnit.MINUTES)
                        .equals(campaignFilterDto.getCreatedAt().truncatedTo(ChronoUnit.MINUTES)));
    }
}
