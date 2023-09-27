package faang.school.projectservice.repository.moment;

import faang.school.projectservice.jpa.MomentJpaRepository;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MomentRepository implements MomentCriteriaRepository {
    private final EntityManager entityManager;
    private final MomentJpaRepository momentJpaRepository;

    @Override
    public Page<Moment> findByProjectsAndDate(List<Long> projectIds, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Moment> cq = cb.createQuery(Moment.class);

        Root<Moment> moment = cq.from(Moment.class);
        Join<Moment, Project> project = moment.join("projects");

        List<Predicate> predicates = new ArrayList<>();

        if (projectIds != null && !projectIds.isEmpty()) {
            predicates.add(project.get("id").in(projectIds));
        }

        if (dateFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(moment.get("date"), dateFrom));
        }

        if (dateTo != null) {
            predicates.add(cb.lessThanOrEqualTo(moment.get("date"), dateTo));
        }

        cq.select(moment).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Moment> momentTypedQuery = entityManager.createQuery(cq);
        int totalRows = momentTypedQuery.getResultList().size();

        momentTypedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        momentTypedQuery.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(momentTypedQuery.getResultList(), pageable, totalRows);
    }

    public void save(Moment moment) {
        momentJpaRepository.save(moment);
    }

    public Optional<Moment> findById(Long momentId) {
        return momentJpaRepository.findById(momentId);
    }

    public Page<Moment> findAll(Pageable pageable) {
        return momentJpaRepository.findAll(pageable);
    }
}
