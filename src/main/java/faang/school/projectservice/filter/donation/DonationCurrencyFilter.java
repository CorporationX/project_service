package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.List;

public class DonationCurrencyFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getCurrency() != null;
    }

    @Override
    public void apply(List<Donation> donations, DonationFilterDto donationFilterDto) {
        donations.removeIf(donation -> !donation.getCurrency().name().equals(donationFilterDto.getCurrency()));
    }
}
