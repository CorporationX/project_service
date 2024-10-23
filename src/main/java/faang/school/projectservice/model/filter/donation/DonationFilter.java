package faang.school.projectservice.model.filter.donation;

import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Donation;

import java.util.stream.Stream;

public interface DonationFilter {

    boolean isApplicable(DonationFilterDto filterDto);

    Stream<Donation> apply(Stream<Donation> stageStream, DonationFilterDto filterDto);
}
