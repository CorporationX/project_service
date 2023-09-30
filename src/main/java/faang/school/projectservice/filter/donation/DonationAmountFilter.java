package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DonationAmountFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getAmount() != null;
    }

    @Override
    public void apply(List<Donation> donations, DonationFilterDto donationFilterDto) {
        donations.removeIf(donation -> !donation.getAmount().equals(donationFilterDto.getAmount()));
    }
}
