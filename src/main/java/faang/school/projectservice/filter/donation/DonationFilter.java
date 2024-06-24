package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.filter.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public interface DonationFilter {

    boolean isAcceptable(DonationFilterDto donationFilterDto);

    Stream<Donation> applyFilter(Stream<Donation> donations, DonationFilterDto donationFilterDto);
}