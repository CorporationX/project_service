package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DonationFromDateFilter implements DonationFilter {

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.fromDonationTime() != null;
    }

    @Override
    public Specification<Donation> apply(DonationFilterDto filter) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("donationTime"), filter.fromDonationTime());
    }
}
