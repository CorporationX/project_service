package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DonationDateFilterTest {
    private DonationDateFilter dateFilter;
    private DonationFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        dateFilter = new DonationDateFilter();
        filterDto = new DonationFilterDto();
    }


    @Test
    void testIsApplicable_shouldReturnTrueWhenDonationDateIsNotNull() {
        filterDto.setDonationDate(LocalDate.now());

        boolean result = dateFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void testIsApplicable_shouldReturnFalseWhenDonationDateIsNull() {
        boolean result = dateFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void testApply_shouldRemoveDonationsWithMatchingDonationDate() {
        LocalDate filterDate = LocalDate.now();
        filterDto.setDonationDate(filterDate);

        Donation donation1 = new Donation();
        donation1.setDonationTime(LocalDateTime.of(filterDate, LocalDateTime.now().toLocalTime()));

        Donation donation2 = new Donation();
        donation2.setDonationTime(LocalDateTime.of(filterDate.plusDays(1), LocalDateTime.now().toLocalTime()));

        List<Donation> donations = new ArrayList<>();
        donations.add(donation1);
        donations.add(donation2);

        dateFilter.apply(donations, filterDto);

        assertEquals(1, donations.size());
        assertEquals(donation2, donations.get(0));
    }

    @Test
    void testApply_shouldNotRemoveDonationsWithDifferentDonationDate() {
        LocalDate filterDate = LocalDate.now();
        filterDto.setDonationDate(filterDate);

        Donation donation1 = new Donation();
        donation1.setDonationTime(LocalDateTime.of(filterDate.plusDays(1), LocalDateTime.now().toLocalTime()));

        Donation donation2 = new Donation();
        donation2.setDonationTime(LocalDateTime.of(filterDate.plusDays(2), LocalDateTime.now().toLocalTime()));

        List<Donation> donations = new ArrayList<>();
        donations.add(donation1);
        donations.add(donation2);

        dateFilter.apply(donations, filterDto);

        assertEquals(2, donations.size());
    }
}