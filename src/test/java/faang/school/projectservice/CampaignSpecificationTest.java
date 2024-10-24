package faang.school.projectservice;

import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.enums.CampaignStatus;
import faang.school.projectservice.model.dto.CampaignFilterDto;
import faang.school.projectservice.specification.CampaignSpecificationBuilder;
import faang.school.projectservice.specification.CampaignSpecifications;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CampaignSpecificationTest {
    @Test
    void testCreatedAtAfterSpecification() {
        LocalDateTime testDate = LocalDateTime.now().minusDays(1);
        Specification<Campaign> spec = CampaignSpecifications.createdAtAfter(testDate);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<?> cq = mock(CriteriaQuery.class);
        Root<Campaign> root = mock(Root.class);
        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("createdAt")).thenReturn(path);
        when(cb.greaterThanOrEqualTo(path, testDate)).thenReturn(predicate);

        assertNotNull(spec.toPredicate(root, cq, cb));
    }

    @Test
    void testBuildSpecificationWithAllFilters() {
        CampaignFilterDto filterDto = new CampaignFilterDto();
        filterDto.setCreatedAtAfter(LocalDateTime.now().minusDays(2));
        filterDto.setCreatedAtBefore(LocalDateTime.now());
        filterDto.setStatus(CampaignStatus.ACTIVE);
        filterDto.setCreatedBy(1L);

        Specification<Campaign> spec = CampaignSpecificationBuilder.buildSpecification(filterDto);

        assertNotNull(spec);
    }
}
