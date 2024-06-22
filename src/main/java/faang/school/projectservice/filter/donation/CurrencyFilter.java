package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.filter.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CurrencyFilter implements DonationFilter{
    @Override
    public boolean isAcceptable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getCurrency() != null;
    }

    @Override
    public Stream<Donation> applyFilter(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(donation -> donation.getCurrency().equals(Currency.valueOf(donationFilterDto.getCurrency())));
    }
}
