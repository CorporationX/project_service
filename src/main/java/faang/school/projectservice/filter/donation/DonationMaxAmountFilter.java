package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Stream;

@Component
public class DonationMaxAmountFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.maxAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(
                donation -> donation.getAmount().compareTo(donationFilterDto.maxAmount()) <= 0
        );
    }
}
