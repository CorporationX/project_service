package faang.school.projectservice.filter.donation.filters;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationFilterStrategy;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public class DonationMaxAmountFilter implements DonationFilterStrategy {

    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getMaxAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(donation -> donation.getAmount().compareTo(donationFilterDto.getMaxAmount()) <= 0);
    }
}
