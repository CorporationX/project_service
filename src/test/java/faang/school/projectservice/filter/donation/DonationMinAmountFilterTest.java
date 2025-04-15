package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DonationMinAmountFilterTest {

    @InjectMocks
    private DonationMinAmountFilter donationMinAmountFilter;

    DonationFilterDto donationFilterDto;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto(
                null,
                BigDecimal.valueOf(5000L),
                null,
                null
        );
    }

    @Test
    void isApplicable() {
        Assertions.assertTrue(donationMinAmountFilter.isApplicable(donationFilterDto));
    }

    @Test
    void apply() {
        Stream<Donation> donationStream = Stream.of(
                buildDonation(1L, BigDecimal.valueOf(1000L), Currency.USD),
                buildDonation(2L, BigDecimal.valueOf(2000L), Currency.EUR),
                buildDonation(3L, BigDecimal.valueOf(3000L), Currency.USD),
                buildDonation(4L, BigDecimal.valueOf(4000L), Currency.EUR),
                buildDonation(5L, BigDecimal.valueOf(5000L), Currency.USD),
                buildDonation(6L, BigDecimal.valueOf(6000L), Currency.EUR),
                buildDonation(7L, BigDecimal.valueOf(7000L), Currency.USD),
                buildDonation(8L, BigDecimal.valueOf(8000L), Currency.EUR),
                buildDonation(9L, BigDecimal.valueOf(9000L), Currency.USD)
        );

        Stream<Donation> expectedStream = Stream.of(
                buildDonation(5L, BigDecimal.valueOf(5000L), Currency.USD),
                buildDonation(6L, BigDecimal.valueOf(6000L), Currency.EUR),
                buildDonation(7L, BigDecimal.valueOf(7000L), Currency.USD),
                buildDonation(8L, BigDecimal.valueOf(8000L), Currency.EUR),
                buildDonation(9L, BigDecimal.valueOf(9000L), Currency.USD)
        );

        Stream<Donation> actualStream = donationMinAmountFilter.apply(donationStream, donationFilterDto);

        Assertions.assertEquals(expectedStream.toList(), actualStream.toList());
    }

    private Donation buildDonation(Long id, BigDecimal amount, Currency currency) {

        Campaign campaign = new Campaign();
        campaign.setId(id);
        campaign.setCurrency(currency);

        return new Donation(
                id,
                8888L,
                amount,
                LocalDate.now().atTime(LocalTime.MIN),
                campaign,
                currency,
                9999L
        );
    }
}