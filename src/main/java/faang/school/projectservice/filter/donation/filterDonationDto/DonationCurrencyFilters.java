package faang.school.projectservice.filter.donation.filterDonationDto;

import faang.school.projectservice.filter.donation.DonationFilters;
import faang.school.projectservice.filter.donation.FilterDonationDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationCurrencyFilters implements DonationFilters {
    @Override
    public boolean isApplicable(FilterDonationDto filterDonationDto) {
        return filterDonationDto.getCurrencyPattern() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, FilterDonationDto filterDto) {
        return donations.filter(donation -> donation.getCurrency().equals(filterDto.getCurrencyPattern()));
    }
}
