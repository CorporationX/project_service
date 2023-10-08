package faang.school.projectservice.filter.donation;


import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DonationAmountFilterTest {
    @Mock
    private DonationRepository donationRepository;
    @InjectMocks
    private DonationAmountFilter filter;

    @Test
    public void testIsApplicableWithNonNullAmount() {
        DonationFilterDto filterDto = new DonationFilterDto();
        filterDto.setAmount(BigDecimal.valueOf(100.0));

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWithNullAmount() {
        DonationFilterDto filterDto = new DonationFilterDto();

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableShouldReturnTrueWhenAmountIsNotNull() {
        DonationFilterDto filterDto = DonationFilterDto.builder()
                .amount(BigDecimal.TEN)
                .build();

        boolean result = filter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void testIsApplicableShouldReturnFalseWhenAmountIsNull() {
        DonationFilterDto filterDto = DonationFilterDto.builder().build();

        boolean result = filter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void testApplyShouldReturnListOfDonationsWithGivenAmount() {
        BigDecimal amount = BigDecimal.valueOf(50);
        DonationFilterDto filterDto = DonationFilterDto.builder().amount(amount).build();

        List<Donation> expectedDonations = Collections.singletonList(new Donation());
        when(donationRepository.findByAmount(amount)).thenReturn(expectedDonations);

        List<Donation> result = filter.apply(filterDto);

        assertEquals(expectedDonations, result);
    }

    @Test
    void testApplyShouldReturnEmptyListWhenNoDonationWithGivenAmount() {
        BigDecimal amount = BigDecimal.valueOf(100);
        DonationFilterDto filterDto = DonationFilterDto.builder().amount(amount).build();

        when(donationRepository.findByAmount(amount)).thenReturn(Collections.emptyList());

        List<Donation> result = filter.apply(filterDto);

        assertEquals(Collections.emptyList(), result);
    }
}