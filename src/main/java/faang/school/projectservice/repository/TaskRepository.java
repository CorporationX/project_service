package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);
    
    Optional<Task> findByJiraIssueKey(String jiraIssueKey);
    
    @Query("SELECT DISTINCT t.project.id FROM Task t WHERE t.project IS NOT NULL")
    List<Long> findDistinctProjectIds();
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId "
            + "AND (:status IS NULL OR t.status = :status) "
            + "AND (:performerUserId IS NULL OR t.performerUserId = :performerUserId)")
    List<Task> findByProjectIdAndFilters(Long projectId, TaskStatus status, Long performerUserId);
}
