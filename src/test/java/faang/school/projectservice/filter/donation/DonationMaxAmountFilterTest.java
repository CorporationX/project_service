package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DonationMaxAmountFilterTest {
    private DonationFilterDto donationFilterDto;
    private DonationMaxAmountFilter filter;
    private Donation donation1;
    private Donation donation2;
    private Donation donation3;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto();
        filter = new DonationMaxAmountFilter();
        donation1 = new Donation();
        donation2 = new Donation();
        donation3 = new Donation();
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenMaxAmountFilterIsNull() {
        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenMaxAmountFilterIsNotNull() {
        donationFilterDto.setMaxAmount(new BigDecimal(500));
        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    void applyTest_shouldFilterDonationsByMaxAmountFilter() {
        donationFilterDto.setMaxAmount(new BigDecimal(100));

        donation1.setAmount(new BigDecimal(99));
        donation2.setAmount(new BigDecimal(5));
        donation3.setAmount(new BigDecimal(500));

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> expected = List.of(donation1, donation2);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(expected, actual);
    }

    @Test
    void applyTest_shouldReturnEmptyWhenNoDonationsMatchMaxAmountFilter() {
        donationFilterDto.setMaxAmount(new BigDecimal(100));

        donation1.setAmount(new BigDecimal(500));
        donation2.setAmount(new BigDecimal(101));
        donation3.setAmount(new BigDecimal(350));

        List<Donation> input = List.of(donation1, donation2, donation3);
        List<Donation> actual = filter.apply(input.stream(), donationFilterDto).toList();

        assertEquals(List.of(), actual);
    }
}
