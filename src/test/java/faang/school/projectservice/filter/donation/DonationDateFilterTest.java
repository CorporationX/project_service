package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DonationDateFilterTest {
    private DonationFilterDto donationFilterDto;
    private DonationDateFilter filter;
    private Donation donation1;
    private Donation donation2;
    private Donation donation3;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto();
        filter = new DonationDateFilter();
        donation1 = new Donation();
        donation2 = new Donation();
        donation3 = new Donation();
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenDateIsNull() {
        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenDateIsNotNull() {
        donationFilterDto.setDate(LocalDate.of(2025, 2, 5));
        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    void applyTest_shouldFilterDonationsByDate() {
        donationFilterDto.setDate(LocalDate.of(2025, 1, 8));

        donation1.setDonationTime(LocalDateTime.of(2025, 1, 8, 12, 59));
        donation2.setDonationTime(LocalDateTime.of(2025, 2, 8, 12, 59));
        donation3.setDonationTime(LocalDateTime.of(2025, 1, 15, 12, 59));

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> expected = List.of(donation1);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(expected, actual);
    }

    @Test
    void applyTest_shouldReturnEmptyWhenNoDonationsMatchDate() {
        donationFilterDto.setDate(LocalDate.of(2025, 1, 8));

        donation1.setDonationTime(LocalDateTime.of(2025, 1, 9, 12, 59));
        donation2.setDonationTime(LocalDateTime.of(2025, 2, 8, 12, 59));
        donation3.setDonationTime(LocalDateTime.of(2025, 1, 15, 12, 59));

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(List.of(), actual);
    }
}
