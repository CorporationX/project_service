package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Moment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MomentJpaRepository extends JpaRepository<Moment, Long> {

    @Query(nativeQuery = true, value = """
            select m.* from moment m
            where m.id in
            (select moment_id from moment_project where project_id = :projectId)
            """)
    List<Moment> findAllByProjectId(long projectId);

    @Query(nativeQuery = true, value = """
            select m.* from moment m
            where m.id in
            (select moment_id from moment_project where project_id in :projectIds)
            """)
    List<Moment> findAllByProjectIds(List<Long> projectIds, Pageable pageable);
}
