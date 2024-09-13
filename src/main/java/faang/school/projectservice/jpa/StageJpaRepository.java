package faang.school.projectservice.jpa;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageJpaRepository extends JpaRepository<Stage, Long> {
    @Modifying
    @Query("""
            SELECT s FROM Stage s
            join s.project p
            join s.stageRoles sr
            join s.tasks t
            WHERE p.id = :projectId
            AND sr.teamRole IN :roles
            AND t.status IN :taskStatuses 
            """)
    List<Stage> findStagesByProjectAndFilters(
            @Param("projectId") Long projectId,
            @Param("roles") List<TeamRole> roles,
            @Param("taskStatuses") List<TaskStatus> taskStatuses);


    @Modifying
    @Query("""
            DELETE FROM Task t 
            WHERE t.stage.stageId = :stageId 
            """)
    void deleteAllTasksByStageId(@Param("stageId") Long stageId);


    @Modifying
    @Query("""
            UPDATE Task t
            SET t.status = 'CLOSED'
            WHERE t.stage.stageId = :stageId
            """)
    void closeTasksByStageId(@Param("stageId") Long stageId);


    @Modifying
    @Query("""
            UPDATE Task t
            SET t.stage = :consumerStage
            WHERE t.stage.stageId = :providerStageId
            """)
    void reassignTasksFromToStage(@Param("providerStageId") Long providerStageId,
                                  @Param("consumerStage") Stage consumerStage);


    @Query("""
            SELECT s FROM Stage s
            WHERE s.project.id = :projectId
            """)
    List<Stage> findAllStagesByProjectId(@Param("projectId") Long projectId);
}
