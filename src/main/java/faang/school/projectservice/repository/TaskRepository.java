package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    @Query("""
    SELECT t FROM Task t
    WHERE 
        (:status IS NULL OR t.status = :status) AND
        (:performerId IS NULL OR t.performerUserId = :performerId) AND
        (:keyword IS NULL OR 
            LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    List<Task> findFilteredTasks(
            @Param("status") TaskStatus status,
            @Param("performerId") Long performerId,
            @Param("keyword") String keyword
    );
}
