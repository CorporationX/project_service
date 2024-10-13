package faang.school.projectservice.jpa;

import faang.school.projectservice.model.entity.Task;
import faang.school.projectservice.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    List<Long> findUserIdsWithTasksNotInStatuses(Set<Long> projectIds, List<Long> performerUserIds, List<TaskStatus> statuses);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.performerUserId IN :performerUserIds " +
            "AND t.status NOT IN :excludedStatuses")
    List<Task> findTasksByProjectIdAndPerformerUserIdsAndStatusNotIn(Long projectId, List<Long> performerUserIds, List<TaskStatus> excludedStatuses);
}
