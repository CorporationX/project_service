package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DonationFromDateFilterTest {

    private final DonationFromDateFilter filter = new DonationFromDateFilter();
    private final LocalDateTime fromDate = LocalDateTime.now();

    @Mock
    private Root<Donation> root;

    @Mock
    private CriteriaQuery<Donation> query;

    @Mock
    private CriteriaBuilder builder;

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(createFilterDto(fromDate));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveNotApplicable() {
        boolean isApplicable = filter.isApplicable(createFilterDto(null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApply() {
        Specification<Donation> spec = filter.apply(createFilterDto(fromDate));
        spec.toPredicate(root, query, builder);

        verify(builder).greaterThanOrEqualTo(any(), eq(fromDate));
    }

    private DonationFilterDto createFilterDto(LocalDateTime fromDate) {
        return DonationFilterDto.builder()
                .fromDonationTime(fromDate)
                .build();
    }
}
