package faang.school.projectservice.repository;

import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findAllByProjectId(Long projectId);

    List<Task> findAllByProjectId(Long projectId, Specification<Task> specifications);
}
