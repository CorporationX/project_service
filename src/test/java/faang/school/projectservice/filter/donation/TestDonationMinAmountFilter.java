package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class TestDonationMinAmountFilter implements DonationFilter {

    @Override
    public boolean isApplicable(SearchDonationDto searchDonationDto) {
        return true;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, SearchDonationDto searchDonationDto) {
        return donations.filter(donation -> {
            int result = donation.getAmount().compareTo(BigDecimal.valueOf(50L));
            return result >= 0;
        });
    }
}
