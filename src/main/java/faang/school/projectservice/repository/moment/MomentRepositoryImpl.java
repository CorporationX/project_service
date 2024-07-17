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
public class MomentRepositoryImpl implements MomentRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Moment> findAllByProjectIdAndDateFiltered(Long projectId, LocalDateTime start, LocalDateTime endExclusive) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Moment> cq = cb.createQuery(Moment.class);
        Root<Moment> moment = cq.from(Moment.class);

        Join<Project, Moment> momentProject = moment.join("projects");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(momentProject.get("id"), projectId));

        if (start != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(moment.get("date"), cb.literal(start))
            );
        }
        if (endExclusive != null) {
            predicates.add(
                    cb.lessThan(moment.get("date"), cb.literal(endExclusive))
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}
