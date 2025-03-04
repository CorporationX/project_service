package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CurrencyFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.getCurrency() != null;
    }

    @Override
    public List<Donation> apply(List<Donation> donations, DonationFilterDto filterDto) {
        return donations.stream()
                .filter(donation -> donation.getCurrency().equals(filterDto.getCurrency()))
                .collect(Collectors.toList());
    }
}
