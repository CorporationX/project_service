package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.domain.Specification;

public interface DonationFilter {

    boolean isApplicable(DonationFilterDto filter);

    Specification<Donation> apply(DonationFilterDto filter);
}
