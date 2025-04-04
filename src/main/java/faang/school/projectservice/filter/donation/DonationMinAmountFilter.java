package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationMinAmountFilter implements DonationFilter {

    @Override
    public boolean isApplicable(SearchDonationDto searchDonationDto) {
        return searchDonationDto.minAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, SearchDonationDto searchDonationDto) {
        return donations.filter(donation -> {
            int result = donation.getAmount().compareTo(searchDonationDto.minAmount());
            return result >= 0;
        });
    }
}
