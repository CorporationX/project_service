package faang.school.projectservice.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import java.util.Objects;
import java.util.function.Supplier;

public class PredicateBuilder {
    private final CriteriaBuilder criteriaBuilder;
    private Predicate predicate;

    public PredicateBuilder(CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
        this.predicate = criteriaBuilder.conjunction();
    }

    public PredicateBuilder addCondition(Supplier<Predicate> conditionSupplier) {
        Predicate condition = conditionSupplier.get();
        if (Objects.nonNull(condition)) {
            predicate = criteriaBuilder.and(predicate, condition);
        }
        return this;
    }

    public Predicate build() {
        return this.predicate;
    }
}
