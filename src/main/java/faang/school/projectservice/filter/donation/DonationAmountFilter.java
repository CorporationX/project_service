package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationAmountFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filtersDto) {
        return filtersDto.amountMin() != null && filtersDto.amountMax() != null
                && filtersDto.amountMin().compareTo(filtersDto.amountMax()) < 0;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filtersDto) {
        return donations.filter(donation -> donation.getAmount().compareTo(filtersDto.amountMin()) >= 0
                && donation.getAmount().compareTo(filtersDto.amountMax()) <= 0);
    }
}