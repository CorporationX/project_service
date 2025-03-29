package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

class DonationCurrencyFilterTest {

    private DonationCurrencyFilter filter;
    private DonationFilterDto donationFilterDto;

    @BeforeEach
    void setUp() {
        filter = new DonationCurrencyFilter();
        donationFilterDto = new DonationFilterDto();
        donationFilterDto.setCurrency(Currency.USD);
    }

    @Test
    @DisplayName("Фильтр с без валюты")
    void isApplicableNullCurrency() {
        donationFilterDto.setCurrency(null);

        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр с корректными входными данными")
    void isApplicableTrue() {
        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Успешное применение фильтра")
    void testApplyFilter() {
        Stream<Donation> donations = Stream.of(
                Donation.builder().currency(Currency.USD).build(),
                Donation.builder().currency(Currency.USD).build(),
                Donation.builder().currency(Currency.EUR).build()
        );

        Stream<Donation> filteredDonations = filter.apply(donations, donationFilterDto);
        List<Donation> result = filteredDonations.toList();

        assertEquals(2, result.size());
    }
}