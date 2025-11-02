package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationCurrencyFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filtersDto) {
        return filtersDto.currency() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filtersDto) {
        return donations.filter(donation -> donation.getCurrency().equals(filtersDto.currency()));
    }
}