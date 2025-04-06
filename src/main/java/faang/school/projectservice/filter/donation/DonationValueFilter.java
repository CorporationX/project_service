package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Фильтр для максимального или минимального доната.
 */
@Component
public class DonationValueFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.getExtremumType() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter) {
        if (filter.getExtremumType() == ExtremumType.MAX) {
            return donations.max(Comparator.comparing(Donation::getAmount))
                    .stream();
        } else {
            return donations.min(Comparator.comparing(Donation::getAmount))
                    .stream();
        }
    }
}
