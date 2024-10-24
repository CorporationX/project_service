package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CreationDateToFilter implements CampaignFilter {

    @Override
    public boolean isAccepted(CampaignFilterDto filter) {
        return filter != null && filter.getCreatedTo() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> achievementStream, CampaignFilterDto filter) {
        return achievementStream.filter(campaign ->
                filter.getCreatedTo().isAfter(campaign.getCreatedAt()) ||
                filter.getCreatedTo().isEqual(campaign.getCreatedAt())
        );
    }
}
