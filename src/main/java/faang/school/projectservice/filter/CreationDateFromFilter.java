package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CreationDateFromFilter implements CampaignFilter {

    @Override
    public boolean isAccepted(CampaignFilterDto filter) {
        return filter != null && filter.getCreatedFrom() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaignStream, CampaignFilterDto filter) {
        return campaignStream.filter(campaign ->
                filter.getCreatedFrom().isBefore(campaign.getCreatedAt()) ||
                filter.getCreatedFrom().isEqual(campaign.getCreatedAt())
        );
    }
}

