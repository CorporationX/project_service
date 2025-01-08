package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TaskJpaRepository;
import faang.school.projectservice.model.Task;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private final TaskJpaRepository taskJpaRepository;

    public Task save(String name, String description, String status, Long reporterUserId,
                     Long performerUserId, Long parentTaskId, Long projectId, Long stageId) {
        return taskJpaRepository.save(name, description, status, reporterUserId,
                performerUserId, parentTaskId, projectId, stageId);
    }

    public void linkTask(Long taskId, Long linkedTaskId) {
        taskJpaRepository.linkTask(taskId, linkedTaskId);
    }

    public void unlinkTask(Long taskId) {
        taskJpaRepository.unlinkTask(taskId);
    }

    public void update(Long taskId, String description, String status,
                       Integer minutesTracked, Long reporterUserId, Long parentTaskId) {
        taskJpaRepository.updateTask(taskId, description, status, minutesTracked,
                reporterUserId, parentTaskId);
    }

    public Task getById(Long taskId) {
        return taskJpaRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Task not found by id: %s", taskId)));
    }

    public List<Task> findAll() {
        return taskJpaRepository.findAll();
    }

    public List<Task> findAllByProjectId(Long projectId){
        return taskJpaRepository.findAllByProjectId(projectId);
    }

    public Task save(Task task) {
        return taskJpaRepository.save(task);
    }

    public void saveAll(List<Task> tasks) {
        taskJpaRepository.saveAll(tasks);
    }

    public void delete(Long taskId) {
        taskJpaRepository.deleteById(taskId);
    }


}
