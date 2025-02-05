package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DonationCurrencyFilterTest {
    private DonationFilterDto donationFilterDto;
    private DonationCurrencyFilter filter;
    private Donation donation1;
    private Donation donation2;
    private Donation donation3;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto();
        filter = new DonationCurrencyFilter();
        donation1 = new Donation();
        donation2 = new Donation();
        donation3 = new Donation();
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenCurrencyIsNull() {
        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenCurrencyIsNotNull() {
        donationFilterDto.setCurrency(Currency.USD);
        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    void applyTest_shouldFilterDonationsByCurrency() {
        donationFilterDto.setCurrency(Currency.USD);

        donation1.setCurrency(Currency.EUR);
        donation2.setCurrency(Currency.USD);
        donation3.setCurrency(Currency.USD);

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> expected = List.of(donation2, donation3);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(expected, actual);
    }

    @Test
    void applyTest_shouldReturnEmptyWhenNoDonationsMatchCurrency() {
        donationFilterDto.setCurrency(Currency.EUR);

        donation1.setCurrency(Currency.USD);
        donation2.setCurrency(Currency.USD);
        donation3.setCurrency(Currency.USD);

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(List.of(), actual);
    }
}
