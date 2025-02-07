package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    @Query(nativeQuery = true, value = """
            UPDATE task SET status = :status, updated_at = now()
            WHERE stage_id = :stageId
            """)
    @Modifying
    void updateStatus(long stageId, String status);
}
