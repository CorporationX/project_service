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
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DonationDateFilterTest {

    @InjectMocks
    private DonationDateFilter donationDateFilter;

    DonationFilterDto donationFilterDto;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto(
                null,
                null,
                null,
                LocalDate.now()
        );
    }

    @Test
    void isApplicable() {
        Assertions.assertTrue(donationDateFilter.isApplicable(donationFilterDto));
    }

    @Test
    void apply() {

        Stream<Donation> donationStream = Stream.of(
                buildDonationYesterday(1L, BigDecimal.valueOf(1000L), Currency.USD),
                buildDonationToday(2L, BigDecimal.valueOf(2000L), Currency.USD),
                buildDonationYesterday(3L, BigDecimal.valueOf(3000L), Currency.EUR),
                buildDonationToday(4L, BigDecimal.valueOf(4000L), Currency.EUR),
                buildDonationYesterday(5L, BigDecimal.valueOf(5000L), Currency.USD),
                buildDonationToday(6L, BigDecimal.valueOf(6000L), Currency.USD),
                buildDonationYesterday(7L, BigDecimal.valueOf(7000L), Currency.EUR),
                buildDonationToday(8L, BigDecimal.valueOf(8000L), Currency.EUR),
                buildDonationYesterday(9L, BigDecimal.valueOf(9000L), Currency.USD)
        );

        Stream<Donation> expectedStream = Stream.of(
                buildDonationToday(2L, BigDecimal.valueOf(2000L), Currency.USD),
                buildDonationToday(4L, BigDecimal.valueOf(4000L), Currency.EUR),
                buildDonationToday(6L, BigDecimal.valueOf(6000L), Currency.USD),
                buildDonationToday(8L, BigDecimal.valueOf(8000L), Currency.EUR)
        );

        Stream<Donation> actualStream = donationDateFilter.apply(donationStream, donationFilterDto);

        Assertions.assertEquals(expectedStream.toList(), actualStream.toList());
    }

    private Donation buildDonationYesterday(Long id, BigDecimal amount, Currency currency) {

        Campaign campaign = new Campaign();
        campaign.setId(id);
        campaign.setCurrency(currency);

        return new Donation(
                id,
                8888L,
                amount,
                LocalDateTime.now().minusDays(1),
                campaign,
                currency,
                9999L
        );
    }

    private Donation buildDonationToday(Long id, BigDecimal amount, Currency currency) {

        Campaign campaign = new Campaign();
        campaign.setId(id);
        campaign.setCurrency(currency);

        return new Donation(
                id,
                8888L,
                amount,
                LocalDateTime.now(),
                campaign,
                currency,
                9999L
        );
    }
}