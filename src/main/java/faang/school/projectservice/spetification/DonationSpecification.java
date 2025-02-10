package faang.school.projectservice.spetification;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationFilter;
import faang.school.projectservice.model.Donation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DonationSpecification {
    public Specification<Donation> build(long userId, DonationFilter filter) {
        return withUserId(userId)
                .and(withAmountGt(filter.amountGt()))
                .and(withAmountLt(filter.amountLt()))
                .and(withDateAfter(filter.creationDate()))
                .and(withCurrency(filter.currency()));
    }

    public Specification<Donation> withUserId(Long userId) {
        return userId == null ? (root, query, cb) -> cb.conjunction() : (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public Specification<Donation> withAmountGt(BigDecimal amountGt) {
        return (root, query, cb) -> amountGt == null ? cb.conjunction() : cb.greaterThan(root.get("amount"), amountGt);
    }

    public Specification<Donation> withAmountLt(BigDecimal amountLt) {
        return (root, query, cb) -> amountLt == null ? cb.conjunction() : cb.lessThan(root.get("amount"), amountLt);
    }

    public static Specification<Donation> withDateAfter(LocalDate date) {
        return (root, query, cb) -> date == null ? cb.conjunction() : cb.greaterThan(root.get("donationTime"), date);
    }

    public Specification<Donation> withCurrency(Currency currency) {
        return (root, query, cb) -> currency == null ? cb.conjunction() : cb.equal(root.get("currency"), currency);
    }


}
