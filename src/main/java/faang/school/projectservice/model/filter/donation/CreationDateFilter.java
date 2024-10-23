package faang.school.projectservice.model.filter.donation;

import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CreationDateFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.creationDate() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donationStream, DonationFilterDto filterDto) {
        return donationStream.filter(donation -> donation.getDonationTime().isEqual(filterDto.creationDate()));
    }
}
