package faang.school.projectservice.filter.donation.filters;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public class TestDonationCurrencyFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return true;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter) {
        return donations.filter(donation -> donation.getCurrency() == filter.getCurrency());
    }
}
