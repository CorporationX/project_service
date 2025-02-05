package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationMaxAmountFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filters) {
        return filters.getMaxAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filters) {
        return donations
                .filter(donation -> donation.getAmount().compareTo(filters.getMaxAmount()) <= 0);
    }
}
