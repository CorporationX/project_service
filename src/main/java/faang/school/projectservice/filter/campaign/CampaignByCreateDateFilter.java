package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CampaignByCreateDateFilter implements CampaignFilter {
    private static final String CREATED_AT = "createdAt";

    @Override
    public boolean isApplicable(CampaignFilterDto campaignDtoFilterDto) {
        return campaignDtoFilterDto.getCreatedFrom() != null
                || campaignDtoFilterDto.getCreatedTo() != null;
    }

    @Override
    public Specification<Campaign> apply(CampaignFilterDto campaignFilterDto) {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime from = campaignFilterDto.getCreatedFrom();
            LocalDateTime to = campaignFilterDto.getCreatedTo();

            List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(CREATED_AT), from));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(CREATED_AT), to));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
