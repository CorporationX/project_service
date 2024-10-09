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
import java.util.Optional;

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
        checkTaskParameters(tempTask);
        Task createdTask = taskRepository.save(tempTask);
        log.info("Task created by user: {} in {}", createdTask.getReporterUserId(), createdTask.getCreatedAt());
        return createdTask;
    }

    @Transactional
    public Task updateTask(Task tempTask) {
        checkTaskParameters(tempTask);
        Task updatedTask = taskRepository.save(tempTask);
        log.info("Task updating by user: {} in {}", updatedTask.getReporterUserId(), updatedTask.getUpdatedAt());
        return updatedTask;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasks(Long userId, Long projectId, TaskFilterDto filters) {
        checkUserExists(userId);
        checkProjectExist(projectId);

        if (filters != null) {
            return getFilteredTasks(projectId, filters);
        } else {
            return taskRepository.findAllByProjectId(projectId);
        }
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long taskId, Long userId) {
        checkUserExists(userId);

        return taskRepository.findById(taskId).orElseThrow();
    }

    private List<Task> getFilteredTasks(Long projectId, TaskFilterDto filters) {
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        List<TaskFilter> applicableFilters = taskFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        return tasks.stream()
                .filter(task -> applicableFilters.stream()
                        .allMatch(internshipFilter -> internshipFilter.apply(task, filters)))
                .toList();
    }

    private void checkUserExists(Long userId) {
        if (userServiceClient.getUser(userId) == null) {
            throw new IllegalArgumentException("User does not exist");
        }
    }

    private void checkParentTask(Task tempTask) {
        if (tempTask.getParentTask() != null) {
            Optional<Task> parentTask = taskRepository.findById(tempTask.getParentTask().getId());
            parentTask.ifPresentOrElse(
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
            List<Task> linkedTasksTemp = tempTask.getLinkedTasks();
            linkedTasksTemp.forEach(linkedTaskTemp ->
                taskRepository.findById(linkedTaskTemp.getId()).ifPresentOrElse(
                        linkedTasks::add,
                        () -> {
                            throw new IllegalArgumentException(String.format("Linked task with taskId = %d does not exist",
                                    linkedTaskTemp.getId()));
                        }));
            tempTask.setLinkedTasks(linkedTasks);
        }
    }

    private void checkProjectExist(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project does not exist");
        }
    }

    private void fillProjectIfExist(Task tempTask) {
        Project project = projectRepository.getByIdOrThrow(tempTask.getProject().getId());
        tempTask.setProject(project);
    }

    private void checkStage(Task tempTask) {
        if (tempTask.getStage().getStageId() != null) {
            Stage stage = stageRepository.getById(tempTask.getStage().getStageId());
            tempTask.setStage(stage);
        }
    }

    private void checkTaskParameters(Task tempTask) {
        checkUserExists(tempTask.getPerformerUserId());
        checkUserExists(tempTask.getReporterUserId());
        checkParentTask(tempTask);
        checkLinkedTasks(tempTask);
        fillProjectIfExist(tempTask);
        checkStage(tempTask);
    }
}
