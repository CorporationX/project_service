package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DonationMaxAmountFilter implements DonationFilter {

    @Override
    public Specification<Donation> apply(DonationFilterDto filter) {
        return (root, query, builder) -> {
            if (filter.maxAmount() != null) {
                return builder.lessThanOrEqualTo(root.get("amount"), filter.maxAmount());
            }
            return null;
        };
    }
}
