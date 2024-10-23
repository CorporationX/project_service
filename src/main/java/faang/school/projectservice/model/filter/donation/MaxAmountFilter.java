package faang.school.projectservice.model.filter.donation;

import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MaxAmountFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.maxAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donationStream, DonationFilterDto filterDto) {
        return donationStream.filter(donation -> donation.getAmount().compareTo(filterDto.maxAmount()) <= 0);
    }
}