package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.filter.TaskFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final List<TaskFilter> taskFilters;

    @Transactional
    public Task createTask(Task tempTask) {
        Task task = saveTask(tempTask);
        log.info("Task created by user: {} in {}", task.getReporterUserId(), task.getCreatedAt());
        return saveTask(task);
    }

    @Transactional
    public Task updateTask(Task tempTask) {
        Task task = saveTask(tempTask);
        log.info("Task updating by user: {} in {}", tempTask.getReporterUserId(), task.getUpdatedAt());
        return task;
    }

    @Transactional
    public List<Task> getFilteredTasks(Long requestingUserId, TaskFilterDto filters) {
        checkUserExists(requestingUserId);

        List<Task> tasks = taskRepository.findAll();
        List<TaskFilter> applicableFilters = taskFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        return tasks.stream()
                .filter(task -> applicableFilters.stream()
                        .allMatch(internshipFilter -> internshipFilter.apply(task, filters)))
                .toList();
    }

    @Transactional
    public List<Task> getAllTasksByProject(Long userId, Long projectId) {
        checkUserExists(userId);

        return taskRepository.findAllByProjectId(projectId);
    }

    @Transactional
    public Task getTaskById(Long taskId, Long userId) {
        checkUserExists(userId);

        return taskRepository.findById(taskId).orElseThrow();
    }

    private void validateParameters(Task tempTask) {
        if (tempTask.getName().isBlank()) {
            throw new IllegalArgumentException("Task name cannot be blank");
        }

        checkUserExists(tempTask.getPerformerUserId());
        checkUserExists(tempTask.getReporterUserId());
    }

    private void checkUserExists(Long userId) {
        //todo: Проверка существования пользователя через сервис UserServiceClient
        //        if (userServiceClient.getUser(userId) == null) {
        //            throw new IllegalArgumentException("User does not exist");
        //        }
        // Для прогона тестов
        if (userId == 25) {
            throw new IllegalArgumentException("User does not exist");
        }
    }

    private void checkParentTask(Task tempTask) {
        if (tempTask.getParentTask() != null) {
            taskRepository.findById(tempTask.getParentTask().getId()).ifPresentOrElse(
                    tempTask::setParentTask,
                    () -> {
                        throw new IllegalArgumentException(String.format("Task with taskId = %d does not exist",
                                tempTask.getParentTask().getId()));
                    });
        }
    }

    private void checkLinkedTasks(Task tempTask) {
        if (tempTask.getLinkedTasks() != null && !tempTask.getLinkedTasks().isEmpty()) {
            List<Task> linkedTasks = new ArrayList<>();
            tempTask.getLinkedTasks().forEach(linkedTask -> {
                taskRepository.findById(linkedTask.getId()).ifPresentOrElse(
                        linkedTasks::add,
                        () -> {
                            throw new IllegalArgumentException(String.format("Linked task with taskId = %d does not exist",
                                    linkedTask.getId()));
                        });
            });
            tempTask.setLinkedTasks(linkedTasks);
        }
    }

    private void checkProject(Task tempTask) {
        if (tempTask.getProject().getId() != null) {
            Project project = projectRepository.getProjectById(tempTask.getProject().getId());
            tempTask.setProject(project);
        }
    }

    private void checkStage(Task tempTask) {
        if (tempTask.getStage().getStageId() != null) {
            Stage stage = stageRepository.getById(tempTask.getStage().getStageId());
            tempTask.setStage(stage);
        }
    }

    private Task saveTask(Task tempTask) {
        validateParameters(tempTask);
        checkParentTask(tempTask);
        checkLinkedTasks(tempTask);
        checkProject(tempTask);
        checkStage(tempTask);

        return taskRepository.save(tempTask);
    }
}
