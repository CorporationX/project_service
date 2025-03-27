package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);
}
