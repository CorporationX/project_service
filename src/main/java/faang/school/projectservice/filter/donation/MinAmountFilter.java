package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MinAmountFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.getMinAmount() != null;
    }

    @Override
    public List<Donation> apply(List<Donation> donations, DonationFilterDto filterDto) {
        return donations.stream()
                .filter(donation -> donation.getAmount().compareTo(filterDto.getMinAmount()) >= 0)
                .collect(Collectors.toList());
    }
}