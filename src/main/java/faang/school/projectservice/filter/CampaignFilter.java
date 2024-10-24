package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;

import java.util.stream.Stream;

public interface CampaignFilter {

    boolean isAccepted(CampaignFilterDto filter);

    Stream<Campaign> apply(Stream<Campaign> achievementStream, CampaignFilterDto filter);
}
