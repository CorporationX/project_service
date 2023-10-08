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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DonationCurrencyFilterTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationCurrencyFilter filter;

    @Test
    void testIsApplicableShouldReturnTrueWhenCurrencyIsNotNull() {
        DonationFilterDto filterDto = DonationFilterDto.builder().currency("USD").build();
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableShouldReturnFalseWhenCurrencyIsNull() {
        DonationFilterDto filterDto = DonationFilterDto.builder().build();
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyShouldReturnEmptyListWhenCurrencyIsNull() {
        DonationFilterDto filterDto = DonationFilterDto.builder().build();
        List<Donation> result = filter.apply(filterDto);
        assertTrue(result.isEmpty());
    }

    @Test
    void testApplyShouldReturnFilteredDonationsByCurrency() {
        String currencyCode = "USD";
        DonationFilterDto filterDto = DonationFilterDto.builder().currency(currencyCode).build();
        when(donationRepository.findByCurrency(currencyCode)).thenReturn(Collections.emptyList());

        List<Donation> result = filter.apply(filterDto);

        assertTrue(result.isEmpty());
        verify(donationRepository, times(1)).findByCurrency(currencyCode);
    }
}