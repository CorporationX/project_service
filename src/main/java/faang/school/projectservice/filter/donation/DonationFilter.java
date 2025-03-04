package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.List;

public interface DonationFilter {
    boolean isApplicable(DonationFilterDto filterDto);
    List<Donation> apply(List<Donation> donations, DonationFilterDto filterDto);
}
