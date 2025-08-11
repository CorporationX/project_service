package faang.school.projectservice.repository;

import faang.school.projectservice.model.Moment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MomentRepository extends JpaRepository<Moment, Long> {

    @Query(nativeQuery = true, value = """
            SELECT m.* FROM Moment m
            WHERE m.id in
            (SELECT moment_id FROM moment_project WHERE project_id = :projectId)
            """
    )
    List<Moment> findAllByProjectId(long projectId);
}
