package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test cases of DonationCreationDateFilterTest")
public class DonationCreationDateFilterTest {

    private static final LocalDate LOCAL_DATE_NOW = LocalDate.now();
    private static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();
    
    private final DonationCreationDateFilter donationFilter = new DonationCreationDateFilter();

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
        SearchDonationDto searchDonationDto = new SearchDonationDto(LOCAL_DATE_NOW, null, null, null);

        boolean result = donationFilter.isApplicable(searchDonationDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("apply - without suitable donation")
    public void testApplyNoSuitableDonation() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(LOCAL_DATE_NOW.minusDays(1L), null, null, null);
        Stream<Donation> donations = Stream.of(
                Donation.builder().donationTime(LOCAL_DATE_TIME_NOW).build(),
                Donation.builder().donationTime(LOCAL_DATE_TIME_NOW).build()
        );

        List<Donation> donationList = donationFilter.apply(donations, searchDonationDto).toList();

        assertEquals(0, donationList.size());
    }

    @Test
    @DisplayName("apply - success")
    public void testApplySuccess() {
        SearchDonationDto searchDonationDto = new SearchDonationDto(LOCAL_DATE_NOW, null, null, null);
        Stream<Donation> donations = Stream.of(
                Donation.builder().donationTime(LOCAL_DATE_TIME_NOW).build(),
                Donation.builder().donationTime(LOCAL_DATE_TIME_NOW).build(),
                Donation.builder().donationTime(LOCAL_DATE_TIME_NOW.minusDays(1)).build(),
                Donation.builder().donationTime(LOCAL_DATE_TIME_NOW.minusDays(2)).build()
        );

        List<Donation> donationList = donationFilter.apply(donations, searchDonationDto).toList();

        assertEquals(2, donationList.size());
        assertEquals(LOCAL_DATE_NOW, donationList.get(0).getDonationTime().toLocalDate());
        assertEquals(LOCAL_DATE_NOW, donationList.get(1).getDonationTime().toLocalDate());
    }   
}
