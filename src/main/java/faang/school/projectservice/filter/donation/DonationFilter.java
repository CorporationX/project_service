package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DonationFilter {
    boolean isApplicable(DonationFilterDto donationFilterDto);

    void apply(List<Donation> donations, DonationFilterDto donationFilterDto);
}
