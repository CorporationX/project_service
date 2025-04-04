package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public interface DonationFilter {

    boolean isApplicable(SearchDonationDto searchDonationDto);

    Stream<Donation> apply(Stream<Donation> donations, SearchDonationDto searchDonationDto);
}
