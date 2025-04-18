package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test cases of DonationMaxAmountFilterTest")
public class DonationMaxAmountFilterTest {
    
    private final DonationMaxAmountFilter donationFilter = new DonationMaxAmountFilter();

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
        SearchDonationDto searchDonationDto = new SearchDonationDto(null, null, BigDecimal.ONE, null);

        boolean result = donationFilter.isApplicable(searchDonationDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("apply - without suitable donation")
    public void testApplyNoSuitableDonation() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(null, null, BigDecimal.ONE, null);
        Stream<Donation> donations = Stream.of(
                Donation.builder().amount(BigDecimal.TEN).build(),
                Donation.builder().amount(BigDecimal.TEN).build()
        );

        List<Donation> donationList = donationFilter.apply(donations, searchDonationDto).toList();

        assertEquals(0, donationList.size());
    }

    @Test
    @DisplayName("apply - success")
    public void testApplySuccess() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(null, null, BigDecimal.ONE, null);
        Stream<Donation> donations = Stream.of(
                Donation.builder().amount(BigDecimal.ZERO).build(),
                Donation.builder().amount(BigDecimal.ONE).build(),
                Donation.builder().amount(BigDecimal.TEN).build(),
                Donation.builder().amount(BigDecimal.TEN).build()
        );

        List<Donation> donationList = donationFilter.apply(donations, searchDonationDto).toList();

        assertEquals(2, donationList.size());
        assertTrue(donationList.get(0).getAmount().compareTo(BigDecimal.ONE) <= 0);
        assertTrue(donationList.get(1).getAmount().compareTo(BigDecimal.ONE) <= 0);
    }
}
