package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test cases of DonationCurrencyFilterTest")
public class DonationCurrencyFilterTest {
    
    private final DonationCurrencyFilter donationFilter = new DonationCurrencyFilter();

    @Test
    @DisplayName("isApplicable - creation date is null")
    public void testIsApplicableWithoutDate() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(null, null, null, null);

        boolean result = donationFilter.isApplicable(searchDonationDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("isApplicable - success")
    public void testIsApplicableSuccess() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(null, Currency.USD, null, null);

        boolean result = donationFilter.isApplicable(searchDonationDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("apply - without suitable donation")
    public void testApplyNoSuitableDonation() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(null, Currency.USD, null, null);
        Stream<Donation> donations = Stream.of(
                Donation.builder().currency(Currency.EUR).build(),
                Donation.builder().currency(Currency.EUR).build()
        );

        List<Donation> donationList = donationFilter.apply(donations, searchDonationDto).toList();

        assertEquals(0, donationList.size());
    }

    @Test
    @DisplayName("apply - success")
    public void testApplySuccess() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(null, Currency.USD, null, null);
        Stream<Donation> donations = Stream.of(
                Donation.builder().currency(Currency.USD).build(),
                Donation.builder().currency(Currency.USD).build(),
                Donation.builder().currency(Currency.EUR).build(),
                Donation.builder().currency(Currency.EUR).build()
        );

        List<Donation> donationList = donationFilter.apply(donations, searchDonationDto).toList();

        assertEquals(2, donationList.size());
        assertEquals(Currency.USD, donationList.get(0).getCurrency());
        assertEquals(Currency.USD, donationList.get(1).getCurrency());
    }   
}
