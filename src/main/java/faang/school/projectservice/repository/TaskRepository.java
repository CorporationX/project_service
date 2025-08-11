package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    List<Task> findAllByProjectIdAndPerformerUserId(Long projectId, Long performerId);

    default Task getByIdOrThrow(long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
    }
}
