package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignByCreatorFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFilterDto campaignFilterDto) {
        return campaignFilterDto.getCreatedBy() != null;
    }

    @Override
    public Specification<Campaign> apply(CampaignFilterDto campaignFilterDto) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy"), campaignFilterDto.getCreatedBy());
    }
}
