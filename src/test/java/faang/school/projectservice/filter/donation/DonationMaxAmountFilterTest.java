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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DonationMaxAmountFilterTest {

    private final DonationMaxAmountFilter filter = new DonationMaxAmountFilter();
    private final BigDecimal maxAmount = new BigDecimal("100");

    @Mock
    private Root<Donation> root;

    @Mock
    private CriteriaQuery<Donation> query;

    @Mock
    private CriteriaBuilder builder;

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(createFilterDto(maxAmount));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveNotApplicable() {
        boolean isApplicable = filter.isApplicable(createFilterDto(null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApply() {
        Specification<Donation> spec = filter.apply(createFilterDto(maxAmount));
        spec.toPredicate(root, query, builder);

        verify(builder).lessThanOrEqualTo(any(), eq(maxAmount));
    }

    private DonationFilterDto createFilterDto(BigDecimal maxAmount) {
        return DonationFilterDto.builder()
                .maxAmount(maxAmount)
                .build();
    }
}
