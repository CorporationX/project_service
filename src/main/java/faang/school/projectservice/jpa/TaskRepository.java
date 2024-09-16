package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    @Query("SELECT DISTINCT t.performerUserId FROM Task t " +
            "WHERE t.project.id IN :projectIds " +
            "AND t.performerUserId IN :performerUserIds " +
            "AND t.status NOT IN (:statuses)")
    List<Long> findUserIdsWithTasksNotInStatuses(@Param("projectIds") Set<Long> projectIds,
                                                 @Param("performerUserIds") List<Long> performerUserIds,
                                                 @Param("statuses") List<TaskStatus> statuses);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.performerUserId IN :performerUserIds AND t.status NOT IN :excludedStatuses")
    List<Task> findTasksByProjectIdAndPerformerUserIdsAndStatusNotIn(
            @Param("projectId") Long projectId,
            @Param("performerUserIds") List<Long> performerUserIds,
            @Param("excludedStatuses") List<TaskStatus> excludedStatuses
    );
}
