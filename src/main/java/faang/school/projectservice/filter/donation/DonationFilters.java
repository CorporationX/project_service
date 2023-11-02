package faang.school.projectservice.filter.donation;

import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public interface DonationFilters {
    boolean isApplicable(FilterDonationDto filterDonationDto);
    Stream<Donation> apply(Stream<Donation> donations, FilterDonationDto filterDto);
}
