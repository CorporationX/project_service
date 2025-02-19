package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MinAmountFilterTest {
    private MinAmountFilter minAmountFilter;
    private DonationFilterDto filter;
    private Donation firstDonation;
    private Donation secondDonation;

    @BeforeEach
    void setUp() {
        minAmountFilter = new MinAmountFilter();
        filter = new DonationFilterDto();
        firstDonation = new Donation();
        secondDonation = new Donation();
    }

    @Test
    void testIsApplicableWithNullMinAmount() {
        filter.setMinAmountPattern(null);

        assertFalse(minAmountFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidMinAmount() {
        filter.setMinAmountPattern(BigDecimal.valueOf(1));

        assertTrue(minAmountFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithInvalidMinAmount() {
        filter.setMinAmountPattern(BigDecimal.valueOf(100));
        firstDonation.setAmount(BigDecimal.valueOf(99));
        secondDonation.setAmount(BigDecimal.valueOf(0));
        Stream<Donation> Donations = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = minAmountFilter.apply(Donations, filter).toList();

        assertEquals(0, filteredDonations.size());
    }

    @Test
    void testApplyWithValidMinAmount() {
        filter.setMinAmountPattern(BigDecimal.valueOf(100));
        firstDonation.setAmount(BigDecimal.valueOf(101));
        secondDonation.setAmount(BigDecimal.valueOf(1));
        Stream<Donation> Donations = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = minAmountFilter.apply(Donations, filter).toList();

        assertEquals(1, filteredDonations.size());
    }
}