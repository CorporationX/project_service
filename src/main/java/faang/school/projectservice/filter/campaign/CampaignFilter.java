package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.data.jpa.domain.Specification;

public interface CampaignFilter {

    boolean isApplicable(CampaignFilterDto campaignFilterDto);

    Specification<Campaign> apply(CampaignFilterDto campaignFilterDto);
}
