package faang.school.projectservice.filter.donation.filters;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationFilterStrategy;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public class DonationCreatedAtFilter implements DonationFilterStrategy {

    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(donation ->
                donation.getDonationTime() != null && donation.getDonationTime().isEqual(donationFilterDto.getCreatedAt()));
    }
}
