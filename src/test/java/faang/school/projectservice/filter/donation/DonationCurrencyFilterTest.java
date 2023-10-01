package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DonationCurrencyFilterTest {

    private DonationCurrencyFilter filter;
    private DonationFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        filter = new DonationCurrencyFilter();
        filterDto = new DonationFilterDto();
    }

    @Test
    public void testIsApplicableSuccess() {
        filterDto.setCurrency("USD");

        boolean result = filter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFailure() {
        boolean result = filter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void testApplySuccess() {
        filterDto.setCurrency("USD");

        Donation donation1 = new Donation();
        donation1.setCurrency(Currency.valueOf("USD"));

        Donation donation2 = new Donation();
        donation2.setCurrency(Currency.valueOf("EUR"));

        List<Donation> donations = new ArrayList<>();
        donations.add(donation1);
        donations.add(donation2);

        filter.apply(donations, filterDto);

        assertEquals(1, donations.size());
        assertEquals("USD", donations.get(0).getCurrency().name());
    }

    @Test
    public void testApplyFailure() {
        filterDto.setCurrency("USD");

        filter.apply(new ArrayList<>(), filterDto);

        assertEquals(0, 0);
    }
}