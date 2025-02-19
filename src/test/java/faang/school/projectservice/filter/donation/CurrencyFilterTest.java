package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.payment.Currency;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyFilterTest {
    private CurrencyFilter currencyFilter;
    private DonationFilterDto filter;
    private Donation firstDonation;
    private Donation secondDonation;

    @BeforeEach
    void setUp() {
        currencyFilter = new CurrencyFilter();
        filter = new DonationFilterDto();
        firstDonation = new Donation();
        secondDonation = new Donation();
    }

    @Test
    void testIsApplicableWithNullCurrency() {
        filter.setCurrencyPattern(null);

        assertFalse(currencyFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableWithCurrency() {
        filter.setCurrencyPattern(Currency.USD);

        assertTrue(currencyFilter.isApplicable(filter));
    }

    @Test
    void testApplyWithNonMatchingCurrency() {
        filter.setCurrencyPattern(Currency.EUR);
        firstDonation.setCurrency(Currency.USD);
        secondDonation.setCurrency(Currency.USD);
        Stream<Donation> events = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = currencyFilter.apply(events, filter).toList();

        assertEquals(0, filteredDonations.size());
    }

    @Test
    void testApplyWithMatchingCurrency() {
        filter.setCurrencyPattern(Currency.EUR);
        firstDonation.setCurrency(Currency.EUR);
        secondDonation.setCurrency(Currency.USD);
        Stream<Donation> events = Stream.of(firstDonation, secondDonation);

        List<Donation> filteredDonations = currencyFilter.apply(events, filter).toList();

        assertEquals(1, filteredDonations.size());
    }
}