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

@DisplayName("Тест фильтра DonationValueFilter")
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
        donationFilterDto.setExtremumType(null);

        boolean result = filter.isApplicable(donationFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр с корректными входными данными")
    void isApplicableTrue() {
        donationFilterDto.setExtremumType(ExtremumType.MAX);

        boolean result = filter.isApplicable(donationFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Успешное применение фильтра для поиска максимальной суммы")
    void testApplyFilterMaxValue() {
        donationFilterDto.setExtremumType(ExtremumType.MAX);
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
        donationFilterDto.setExtremumType(ExtremumType.MIN);
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