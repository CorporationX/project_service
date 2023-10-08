package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Builder
@Component
@RequiredArgsConstructor
public class DonationCurrencyFilter implements DonationFilter {
    private final DonationRepository donationRepository;

    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getCurrency() != null;
    }

    @Override
    public List<Donation> apply(DonationFilterDto donationFilterDto) {
        String currency = donationFilterDto.getCurrency();
        return donationRepository.findByCurrency(currency);
    }
}
