package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationDateFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filters) {
        return filters.getDate() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filters) {
        return donations
                .filter(donation -> donation.getDonationTime().toLocalDate().equals(filters.getDate()));
    }
}
