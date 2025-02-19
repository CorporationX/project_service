package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaxAmountFilterTest {
    private MaxAmountFilter maxAmountFilter;
    private DonationFilterDto filter;
    private Donation firstDonation;
    private Donation secondDonation;

    @BeforeEach
    void setUp() {
        maxAmountFilter = new MaxAmountFilter();
        filter = new DonationFilterDto();
        firstDonation = new Donation();
        secondDonation = new Donation();
    }

    @Test
    void testIsApplicableWithNullMaxAmount() {
        filter.setMaxAmountPattern(null);

        assertFalse(maxAmountFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithValidMaxAmount() {
        filter.setMaxAmountPattern(BigDecimal.valueOf(1));

        assertTrue(maxAmountFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithInvalidMaxAmount() {
        filter.setMaxAmountPattern(BigDecimal.valueOf(100));
        firstDonation.setAmount(BigDecimal.valueOf(111));
        secondDonation.setAmount(BigDecimal.valueOf(101));
        Stream<Donation> Donations = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = maxAmountFilter.apply(Donations, filter).toList();

        assertEquals(0, filteredDonations.size());
    }

    @Test
    void testApplyWithValidMaxAmount() {
        filter.setMaxAmountPattern(BigDecimal.valueOf(100));
        firstDonation.setAmount(BigDecimal.valueOf(99));
        secondDonation.setAmount(BigDecimal.valueOf(1));
        Stream<Donation> Donations = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = maxAmountFilter.apply(Donations, filter).toList();

        assertEquals(2, filteredDonations.size());
    }
}