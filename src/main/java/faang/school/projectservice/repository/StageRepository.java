package faang.school.projectservice.repository;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {
    @Query("""
    SELECT DISTINCT stage
    FROM Stage stage
    LEFT JOIN stage.tasks task
    LEFT JOIN stage.stageRoles stageRole
    WHERE stage.project.id = :projectId
      AND (task.status = :taskStatus OR stageRole.teamRole IN :teamRolesList)
""")
    List<Stage> getAllStageByTaskStatusAndTeamRole(
            @Param("projectId") long projectId,
            @Param("taskStatus") TaskStatus taskStatus,
            @Param("teamRolesList") List<TeamRole> teamRolesList
    );
    Stage getStageByStageId(long stageId);
}
