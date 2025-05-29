package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.DonationFilterStrategy;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationMaxAmountFilter implements DonationFilterStrategy {

    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getMaxAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(donation ->
                donation.getAmount().compareTo(donationFilterDto.getMaxAmount()) <= 0);
    }
}
