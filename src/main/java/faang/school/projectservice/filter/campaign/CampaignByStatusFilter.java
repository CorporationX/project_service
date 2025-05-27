package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignByStatusFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignFilterDto campaignFilterDto) {
        return campaignFilterDto.getStatus() != null;
    }

    @Override
    public Specification<Campaign> apply(CampaignFilterDto campaignFilterDto) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), campaignFilterDto.getStatus());
    }
}
