package faang.school.projectservice.repository;

import faang.school.projectservice.model.Moment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MomentRepository extends JpaRepository<Moment, Long>, JpaSpecificationExecutor<Moment> {

    @Query(nativeQuery = true, value = """
    select m.* from moment m
    where m.id in
    (select moment_id from moment_project where project_id = :projectId)
        """)
    List<Moment> findAllByProjectId(long projectId);

    List<Moment> findAll(Specification<Moment> spec);
}
