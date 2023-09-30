package faang.school.projectservice.filter.donation;


import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DonationAmountFilterTest {
    private DonationAmountFilter filter;
    private List<Donation> donations;

    @BeforeEach
    public void setUp() {
        filter = new DonationAmountFilter();
        donations = new ArrayList<>();
    }

    @Test
    public void testIsApplicableWithNonNullAmount() {
        DonationFilterDto filterDto = new DonationFilterDto();
        filterDto.setAmount(BigDecimal.valueOf(100.0));

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWithNullAmount() {
        DonationFilterDto filterDto = new DonationFilterDto();

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    public void testApplyRemovesDonationsWithMatchingAmount() {
        Donation donation1 = new Donation();
        donation1.setAmount(BigDecimal.valueOf(100.0));
        donations.add(donation1);

        Donation donation2 = new Donation();
        donation2.setAmount(BigDecimal.valueOf(200.0));
        donations.add(donation2);

        DonationFilterDto filterDto = new DonationFilterDto();
        filterDto.setAmount(BigDecimal.valueOf(100.0));

        filter.apply(donations, filterDto);

        assertEquals(1, donations.size());
    }

    @Test
    public void testApplyLeavesDonationsWithNonMatchingAmount() {
        Donation donation1 = new Donation();
        donation1.setAmount(BigDecimal.valueOf(100.0));
        donations.add(donation1);

        Donation donation2 = new Donation();
        donation2.setAmount(BigDecimal.valueOf(200.0));
        donations.add(donation2);

        DonationFilterDto filterDto = new DonationFilterDto();
        filterDto.setAmount(BigDecimal.valueOf(300.0));

        filter.apply(donations, filterDto);

        assertEquals(0, donations.size());
    }
}