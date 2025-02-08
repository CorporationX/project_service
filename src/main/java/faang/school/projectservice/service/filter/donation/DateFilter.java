package faang.school.projectservice.service.filter.donation;

import faang.school.projectservice.dto.donate.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DateFilter implements DonationFilter {

    @Override
    public boolean isAcceptable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.createdDate() != null;
    }

    @Override
    public Stream<Donation> accept(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.sorted((d1, d2) -> d2.getDonationTime().compareTo(d1.getDonationTime()));
    }
}
