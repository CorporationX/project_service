package faang.school.projectservice.filter.donation.filters;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.model.Donation;

import java.time.LocalDate;
import java.util.stream.Stream;

public class TestDonationTimeFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return true;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter) {
        LocalDate filterDate = filter.getDonationTime().toLocalDate();
        return donations.filter(donation ->
                donation.getDonationTime().toLocalDate().equals(filterDate)
        );
    }
}
