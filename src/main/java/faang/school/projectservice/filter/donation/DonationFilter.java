package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public interface DonationFilter {
    boolean isApplicable(DonationFilterDto donationFilterDto);

    Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto donationFilterDto);
}
