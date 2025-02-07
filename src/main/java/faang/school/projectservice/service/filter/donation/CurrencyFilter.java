package faang.school.projectservice.service.filter.donation;

import faang.school.projectservice.dto.donate.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CurrencyFilter implements DonationFilter {

    @Override
    public boolean isAcceptable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.currency() != null;
    }

    @Override
    public Stream<Donation> accept(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(donation -> matchesPattern(donationFilterDto.currency(), donation.getCurrency().toString()));
    }

    private boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}
