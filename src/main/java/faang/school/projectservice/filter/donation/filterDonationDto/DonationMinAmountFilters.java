package faang.school.projectservice.filter.donation.filterDonationDto;

import faang.school.projectservice.filter.donation.DonationFilters;
import faang.school.projectservice.filter.donation.FilterDonationDto;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public class DonationMinAmountFilters implements DonationFilters {
    @Override
    public boolean isApplicable(FilterDonationDto filterDonationDto) {
        return filterDonationDto.getMinAmountPattern() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, FilterDonationDto filterDto) {
        return donations.filter(donation -> donation.getAmount().compareTo(filterDto.getMinAmountPattern()) >= 0);
    }
}
