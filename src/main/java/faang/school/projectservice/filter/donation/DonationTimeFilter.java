package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class DonationTimeFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.getDonationTime() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter) {
        return donations.filter(donation -> {
            LocalDateTime donationTime = donation.getDonationTime();
            LocalDateTime filterTime = filter.getDonationTime();
            return donationTime.isEqual(filterTime);
        });
    }
}
