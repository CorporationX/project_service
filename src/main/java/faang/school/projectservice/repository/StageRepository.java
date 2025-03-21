package faang.school.projectservice.repository;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {
    @Query(nativeQuery = true,value = """
                SELECT s.* FROM Stage s INNER JOIN StageRoles sr ON s.stageRoles = sr.id 
                INNER JOIN Tasks t ON s.tasks = t.id where sr.teamRole = :roles and t.status = :status
            """)
    List<Stage> findStagesByRolesAndStatus(List<TeamRole> roles, List<TaskStatus> status);
}
