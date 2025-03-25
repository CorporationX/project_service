package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DonationCurrencyFilter implements DonationFilter {

    @Override
    public Specification<Donation> apply(DonationFilterDto filter) {
        return (root, query, builder) -> {
            if (filter.currency() != null) {
                return builder.equal(root.get("currency"), filter.currency());
            }
            return null;
        };
    }
}
