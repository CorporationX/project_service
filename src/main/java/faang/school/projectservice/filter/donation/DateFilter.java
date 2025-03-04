package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.List;
import java.util.stream.Collectors;

public class DateFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.getDonationDate() != null;
    }

    @Override
    public List<Donation> apply(List<Donation> donations, DonationFilterDto filterDto) {
        return donations.stream()
                .filter(donation -> donation.getDonationTime().toLocalDate().isEqual(filterDto.getDonationDate().toLocalDate()))
                .collect(Collectors.toList());
    }
}
