package faang.school.projectservice.dto.moment;

import faang.school.projectservice.model.Moment;
import org.springframework.data.jpa.domain.Specification;

import static faang.school.projectservice.service.filter.moment.MomentSpecification.*;

public record MomentFilter(Long projectId,
                           Integer month,
                           Integer year) {

    public Specification<Moment> toSpecification() {
        Specification<Moment> spec = Specification.where(null);
        return spec.and(projectEqual(this.projectId))
                .and(monthEqual(this.month))
                .and(yearEqual(this.year));
    }
}
