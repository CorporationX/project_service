package faang.school.projectservice.repository.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class CustomMomentRepositoryImpl implements CustomMomentRepository {

    private final EntityManager em;

    @Override
    public List<Moment> findAllByProjectIdAndDateBetween(
            Long projectId, LocalDateTime start, LocalDateTime endExclusive) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Moment> criteriaQuery = criteriaBuilder.createQuery(Moment.class);
        Root<Moment> momentRoot = criteriaQuery.from(Moment.class);

        Join<Project, Moment> momentProjectJoin = momentRoot.join("projects");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(momentProjectJoin.get("id"), projectId));

        if (start != null) {
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(momentRoot.get("date"), criteriaBuilder.literal(start))
            );
        }
        if (endExclusive != null) {
            predicates.add(
                    criteriaBuilder.lessThan(momentRoot.get("date"), criteriaBuilder.literal(endExclusive))
            );
        }

        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
