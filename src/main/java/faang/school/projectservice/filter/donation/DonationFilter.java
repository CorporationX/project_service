package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import jakarta.validation.constraints.NotNull;

import java.util.stream.Stream;

public interface DonationFilter {

    boolean isApplicable(@NotNull DonationFilterDto filter);

    Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter);
}
