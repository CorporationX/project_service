package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DonationTimeFilterTest {

    private DonationTimeFilter filter;
    private DonationFilterDto donationFilterDto;

    @BeforeEach
    void setUp() {
        filter = new DonationTimeFilter();
        donationFilterDto = new DonationFilterDto();
    }

    @Test
    @DisplayName("Фильтр без даты")
    void isApplicableNullValue() {
        donationFilterDto.setValue(null);

        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр с корректными входными данными")
    void isApplicableTrue() {
        donationFilterDto.setDonationTime(LocalDateTime.parse("2025-03-28T10:15:30"));

        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Успешное применение фильтра для поиска по дате")
    void testApplySuccessAllMatches() {
        donationFilterDto.setDonationTime(LocalDateTime.parse("2025-03-28T10:15:30"));
        Stream<Donation> donations = Stream.of(
                Donation.builder().donationTime(LocalDateTime.parse("2025-03-28T10:15:30")).build(),
                Donation.builder().donationTime(LocalDateTime.parse("2025-03-28T11:15:30")).build(),
                Donation.builder().donationTime(LocalDateTime.parse("2025-03-28T12:15:30")).build()
        );

        Stream<Donation> filteredDonations = filter.apply(donations, donationFilterDto);
        List<Donation> result = filteredDonations.toList();

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Успешное применение фильтра для поиска по дате")
    void testApplySuccessNoneMatches() {
        donationFilterDto.setDonationTime(LocalDateTime.parse("2025-03-28T10:15:30"));
        Stream<Donation> donations = Stream.of(
                Donation.builder().donationTime(LocalDateTime.parse("2025-07-05T10:15:30")).build(),
                Donation.builder().donationTime(LocalDateTime.parse("2025-01-10T11:15:30")).build(),
                Donation.builder().donationTime(LocalDateTime.parse("2025-03-27T12:15:30")).build()
        );

        Stream<Donation> filteredDonations = filter.apply(donations, donationFilterDto);
        List<Donation> result = filteredDonations.toList();

        assertEquals(0, result.size());
    }
}