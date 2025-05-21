package faang.school.projectservice.filter.donation.filters;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationFilterStrategy;
import faang.school.projectservice.model.Donation;

import java.util.Objects;
import java.util.stream.Stream;

public class DonationCurrencyFilter implements DonationFilterStrategy {

    @Override
    public boolean isApplicable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.getCurrency() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(donation -> Objects.equals(donation.getCurrency(), donationFilterDto.getCurrency()));
    }
}
