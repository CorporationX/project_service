package faang.school.projectservice.model.filter.donation;

import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CurrencyFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.currency() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donationStream, DonationFilterDto filterDto) {
        return donationStream.filter(donation -> donation.getCurrency() == filterDto.currency());
    }
}
