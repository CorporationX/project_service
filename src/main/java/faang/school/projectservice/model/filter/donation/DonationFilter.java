package faang.school.projectservice.model.filter.donation;

import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Donation;
import faang.school.projectservice.model.entity.stage.Stage;

import java.util.stream.Stream;

public interface DonationFilter {

    boolean isApplicable();

    Stream<Donation> apply(Stream<Stage> stageStream, DonationFilterDto filterDto);
}
