package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationMaxAmountFilter implements DonationFilter {

    @Override
    public boolean isApplicable(SearchDonationDto searchDonationDto) {
        return searchDonationDto.maxAmount() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, SearchDonationDto searchDonationDto) {
        return donations.filter(donation -> {
            int result = donation.getAmount().compareTo(searchDonationDto.maxAmount());
            return result <= 0;
        });
    }
}
