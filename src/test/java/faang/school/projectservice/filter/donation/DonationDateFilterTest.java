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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DonationDateFilterTest {
    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationDateFilter filter;

    @Test
    public void testIsApplicableShouldReturnTrueWhenDonationDateIsNotNull() {
        DonationFilterDto donationFilterDto = DonationFilterDto.builder()
                .donationDate(LocalDate.now())
                .build();

        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableShouldReturnFalseWhenDonationDateIsNull() {
        DonationFilterDto donationFilterDto = DonationFilterDto.builder().build();

        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApplyShouldReturnFilteredDonationsWhenDonationDateIsNotNull() {
        LocalDate filterDate = LocalDate.now();
        DonationFilterDto donationFilterDto = DonationFilterDto.builder()
                .donationDate(filterDate)
                .build();
        List<Donation> expectedDonations = Collections.singletonList(new Donation());

        when(donationRepository.findByDonationDate(filterDate)).thenReturn(expectedDonations);

        List<Donation> result = filter.apply(donationFilterDto);

        assertEquals(expectedDonations, result);
        verify(donationRepository, times(1)).findByDonationDate(filterDate);
    }

    @Test
    public void testApplyShouldReturnEmptyListWhenDonationDateIsNull() {
        DonationFilterDto donationFilterDto = DonationFilterDto.builder().build();

        List<Donation> result = filter.apply(donationFilterDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}