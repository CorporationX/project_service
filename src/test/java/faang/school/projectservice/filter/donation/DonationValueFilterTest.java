package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DonationValueFilterTest {

    private DonationFilterDto donationFilterDto;
    private DonationValueFilter filter;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto();
        filter = new DonationValueFilter();
    }

    @Test
    @DisplayName("Фильтр без значения")
    void isApplicableNullValue() {
        donationFilterDto.setValue(null);

        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр с корректными входными данными")
    void isApplicableTrue() {
        donationFilterDto.setValue(Value.MAX);

        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Успешное применение фильтра для поиска максимальной суммы")
    void testApplyFilterMaxValue() {
        donationFilterDto.setValue(Value.MAX);
        Stream<Donation> donations = Stream.of(
                Donation.builder().amount(BigDecimal.valueOf(100)).build(),
                Donation.builder().amount(BigDecimal.valueOf(200)).build(),
                Donation.builder().amount(BigDecimal.valueOf(300)).build()
        );

        Stream<Donation> filteredDonations = filter.apply(donations, donationFilterDto);
        List<Donation> result = filteredDonations.toList();

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(300), result.get(0).getAmount());
    }

    @Test
    @DisplayName("Успешное применение фильтра для поиска максимальной суммы")
    void testApplyFilterMinValue() {
        donationFilterDto.setValue(Value.MIN);
        Stream<Donation> donations = Stream.of(
                Donation.builder().amount(BigDecimal.valueOf(100)).build(),
                Donation.builder().amount(BigDecimal.valueOf(200)).build(),
                Donation.builder().amount(BigDecimal.valueOf(300)).build()
        );

        Stream<Donation> filteredDonations = filter.apply(donations, donationFilterDto);
        List<Donation> result = filteredDonations.toList();

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(100), result.get(0).getAmount());
    }
}