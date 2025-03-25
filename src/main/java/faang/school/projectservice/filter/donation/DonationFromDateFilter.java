package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DonationFromDateFilter implements DonationFilter {

    @Override
    public Specification<Donation> apply(DonationFilterDto filter) {
        return (root, query, builder) -> {
            if (filter.fromDonationTime() != null) {
                return builder.greaterThanOrEqualTo(root.get("donationTime"), filter.fromDonationTime());
            }
            return null;
        };
    }
}
