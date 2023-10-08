package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.List;

public interface DonationFilter {
    boolean isApplicable(DonationFilterDto donationFilterDto);

    List<Donation> apply(DonationFilterDto donationFilterDto);
}
