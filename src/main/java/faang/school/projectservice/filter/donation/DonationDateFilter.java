package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DonationDateFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getDonationDate() != null;
    }

    @Override
    public void apply(List<Donation> donations, DonationFilterDto donationFilterDto) {
        LocalDate filterDate = donationFilterDto.getDonationDate();

        donations.removeIf(donation -> donation.getDonationTime().toLocalDate().isEqual(filterDate));
    }
}
