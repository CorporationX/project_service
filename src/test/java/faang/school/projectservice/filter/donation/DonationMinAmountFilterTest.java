package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DonationMinAmountFilterTest {
    private DonationFilterDto donationFilterDto;
    private DonationMinAmountFilter filter;
    private Donation donation1;
    private Donation donation2;
    private Donation donation3;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto();
        filter = new DonationMinAmountFilter();
        donation1 = new Donation();
        donation2 = new Donation();
        donation3 = new Donation();
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenMinAmountFilterIsNull() {
        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenMinAmountFilterIsNotNull() {
        donationFilterDto.setMinAmount(new BigDecimal(50));
        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    void applyTest_shouldFilterDonationsByMinAmountFilter() {
        donationFilterDto.setMinAmount(new BigDecimal(100));

        donation1.setAmount(new BigDecimal(99));
        donation2.setAmount(new BigDecimal(5));
        donation3.setAmount(new BigDecimal(500));

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> expected = List.of(donation3);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(expected, actual);
    }

    @Test
    void applyTest_shouldReturnEmptyWhenNoDonationsMatchMinAmountFilter() {
        donationFilterDto.setMinAmount(new BigDecimal(100));

        donation1.setAmount(new BigDecimal(10));
        donation2.setAmount(new BigDecimal(30));
        donation3.setAmount(new BigDecimal(99));

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(List.of(), actual);
    }
}
