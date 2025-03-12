package faang.school.projectservice.service;

import faang.school.projectservice.event.TaskCompletedEvent;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.publisher.TaskEventPublisher;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;

    @Transactional
    public void completeTask(Long userId, Long taskId, Long projectId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача с id: " + taskId + " не существует"));

        if (task.getStatus() == TaskStatus.DONE) {
            return;
        }

        task.setStatus(TaskStatus.DONE);
        taskRepository.save(task);

        taskEventPublisher.publish(new TaskCompletedEvent(userId, taskId, projectId));
    }
}
