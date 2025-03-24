package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    List<Task> findByIdIn(List<Long> taskIds);

    void deleteByStage(Stage stage);

    List<Task> findByStage(Stage stage);
}
