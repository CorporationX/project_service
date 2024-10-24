package faang.school.projectservice.specification;

import faang.school.projectservice.model.dto.CampaignFilterDto;
import faang.school.projectservice.model.entity.Campaign;
import org.springframework.data.jpa.domain.Specification;

public class CampaignSpecificationBuilder {

    public static Specification<Campaign> buildSpecification(CampaignFilterDto campaignFilterDto) {
        Specification<Campaign> spec = Specification.where(null);

        if (campaignFilterDto.getCreatedAtAfter() != null) {
            spec = spec.and(CampaignSpecifications.createdAtAfter(campaignFilterDto.getCreatedAtAfter()));
        }

        if (campaignFilterDto.getCreatedAtBefore() != null) {
            spec = spec.and(CampaignSpecifications.createdAtBefore(campaignFilterDto.getCreatedAtBefore()));
        }

        if (campaignFilterDto.getStatus() != null) {
            spec = spec.and(CampaignSpecifications.statusEquals(campaignFilterDto.getStatus()));
        }

        if (campaignFilterDto.getCreatedBy() != null) {
            spec = spec.and(CampaignSpecifications.createdByEquals(campaignFilterDto.getCreatedBy()));
        }

        return spec;
    }
}

