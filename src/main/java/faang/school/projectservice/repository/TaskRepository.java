package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    @Query("SELECT t FROM Task t WHERE " +
            "(:projectId IS NULL OR t.project.id = :projectId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:performerUserId IS NULL OR t.performerUserId = :performerUserId) AND " +
            "(:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Task> findTasksByFilters(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("performerUserId") Long performerUserId,
            @Param("keyword") String keyword
    );
}