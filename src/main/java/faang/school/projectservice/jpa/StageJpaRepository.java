package faang.school.projectservice.jpa;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageJpaRepository extends JpaRepository<Stage, Long> {

    List<Stage> findByStageRoles(StageRoles role);

    @Query("SELECT DISTINCT s FROM Stage s " +
            "JOIN s.stageRoles sr " +
            "JOIN s.tasks t " +
            "WHERE sr.teamRole IN :roles " +
            "AND t.status IN :taskStatuses")
    List<Stage> findByRolesAndTaskStatuses(List<TeamRole> roles,
                                           List<TaskStatus> taskStatuses);
}
