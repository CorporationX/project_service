package faang.school.projectservice.model.filter.donation;

import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MinAmountFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.minAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> stageStream, DonationFilterDto filterDto) {
        return stageStream.filter(donation -> donation.getAmount().compareTo(filterDto.minAmount()) >= 0);
    }
}
