package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Stream;

@Component
public class DonationValueFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.getValue() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter) {
        if (filter.getValue() == Value.MAX) {
            return donations.max(Comparator.comparing(Donation::getAmount))
                    .stream();
        } else {
            return donations.min(Comparator.comparing(Donation::getAmount))
                    .stream();
        }
    }
}
