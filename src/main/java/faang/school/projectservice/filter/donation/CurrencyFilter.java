package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.DonationFilter;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CurrencyFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.getCurrencyPattern() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> elements, DonationFilterDto filter) {
        return elements.filter(donation ->
                donation.getCurrency().equals(filter.getCurrencyPattern()));
    }
}
