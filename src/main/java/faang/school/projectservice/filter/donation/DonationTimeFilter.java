package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationTimeFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filtersDto) {
        return filtersDto.donationTime() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filtersDto) {
        return donations.filter(donation -> donation.getDonationTime().equals(filtersDto.donationTime()));
    }
}