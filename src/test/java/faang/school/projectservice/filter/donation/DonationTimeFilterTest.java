package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DonationTimeFilterTest {
    private DonationTimeFilter donationTimeFilter;
    private DonationFilterDto filter;
    private Donation firstDonation;
    private Donation secondDonation;

    @BeforeEach
    void setUp() {
        donationTimeFilter = new DonationTimeFilter();
        filter = new DonationFilterDto();
        firstDonation = new Donation();
        secondDonation = new Donation();
    }

    @Test
    void testIsApplicableWithNullDate() {
        filter.setDonationTimePattern(null);

        assertFalse(donationTimeFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidDate() {
        filter.setDonationTimePattern(LocalDateTime.now().plusDays(1));

        assertTrue(donationTimeFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithInvalidDate() {
        filter.setDonationTimePattern(LocalDateTime.now());
        firstDonation.setDonationTime(LocalDateTime.now().plusMinutes(10));
        secondDonation.setDonationTime(LocalDateTime.now().plusDays(1));
        Stream<Donation> Donations = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = donationTimeFilter.apply(Donations, filter).toList();

        assertEquals(0, filteredDonations.size());
    }

    @Test
    void testApplyWithValidDate() {
        filter.setDonationTimePattern(LocalDateTime.of(2000, 12, 22, 14, 22));
        firstDonation.setDonationTime(LocalDateTime.of(2000, 12, 22, 14, 22));
        secondDonation.setDonationTime(LocalDateTime.now());
        Stream<Donation> Donations = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = donationTimeFilter.apply(Donations, filter).toList();

        assertEquals(1, filteredDonations.size());
    }
}