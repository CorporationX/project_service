package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * Фильтр для доната по дате
 */
@Component
public class DonationTimeFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.getDonationTime() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter) {
        LocalDate filterDate = filter.getDonationTime().toLocalDate();
        return donations.filter(donation ->
                donation.getDonationTime().toLocalDate().equals(filterDate)
        );
    }
}
