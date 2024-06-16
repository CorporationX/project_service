package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    @Query(nativeQuery = true, value = """
            UPDATE task \n" +
            "SET status = :taskStatus \n" +
            "WHERE stage_id = :stageId
            """)
    void updateStatusByStageId(long stageId, TaskStatus taskStatus);

    @Query(nativeQuery = true, value = """
            DELETE FROM task
            WHERE stage_id = :stageId
            """)
    void deleteAllByStageId(long stageId);

    @Query(nativeQuery = true, value = """
            UPDATE task \n
            SET stage_id = :stageToMigrateId \n
            WHERE id IN( \n
                        SELECT t.id \n
                        FROM task t \n
            			WHERE t.stage_id = :stageId \n
            		   )
            """)
    void updateStageId(long stageId, long stageToMigrateId);
}
