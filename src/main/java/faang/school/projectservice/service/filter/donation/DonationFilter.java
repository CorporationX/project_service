package faang.school.projectservice.service.filter.donation;


import faang.school.projectservice.dto.donate.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public interface DonationFilter {
    boolean isAcceptable(DonationFilterDto donationFilterDto);

    Stream<Donation> accept(Stream<Donation> donations, DonationFilterDto donationFilterDto);
}