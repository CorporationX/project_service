package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface StageRepository extends JpaRepository<Stage, Long> {
    @Query(nativeQuery = true, value = """
        SELECT ps.*
        FROM project_stage ps
        INNER JOIN project_stage_roles psr ON ps.project_stage_id = psr.project_stage_id
        INNER JOIN task t ON t.project_id = ps.project_id
        WHERE psr.role IN (:roles)
          AND t.status IN (:status)
    """)
    List<Stage> findStagesByRolesAndTaskStatus(
            @Param("roles") List<TeamRole> roles,
            @Param("status") List<TaskStatus> status
    );

}
