package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;

import java.time.LocalDate;
import java.util.stream.Stream;

public class TestDonationCreationDateFilter implements DonationFilter {

    @Override
    public boolean isApplicable(SearchDonationDto searchDonationDto) {
        return true;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donations, SearchDonationDto searchDonationDto) {
        return donations.filter(donation -> donation.getDonationTime().toLocalDate().equals(LocalDate.now()));
    }
}
