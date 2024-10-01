package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.filter.TaskFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final List<TaskFilter> taskFilters;


    public Task createTask(Task tempTask) {
        validateParameters(tempTask);

        if (tempTask.getParentTask() != null) {
            taskRepository.findById(tempTask.getParentTask().getId()).ifPresentOrElse(
                    tempTask::setParentTask,
                    () -> {
                        throw new IllegalArgumentException(String.format("Task with taskId = %d does not exist",
                                tempTask.getParentTask().getId()));
                    });
        }

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

        if (tempTask.getProject().getId() != null) {
            tempTask.setProject(projectRepository.getProjectById(tempTask.getProject().getId()));
        }

        if (tempTask.getStage().getStageId() != null) {
            Stage stage = stageRepository.getById(tempTask.getStage().getStageId());
            tempTask.setStage(stage);
        }

        return taskRepository.save(tempTask);
    }

    public List<Task> getFilteredTasks(Long requestingUserId, TaskFilterDto filters) {

        //todo: допустим, что пользователя проверили

        List<Task> tasks = taskRepository.findAll();

        List<TaskFilter> applicableFilters = taskFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        return tasks.stream()
                .filter(task -> applicableFilters.stream()
                        .allMatch(internshipFilter -> internshipFilter.apply(task, filters)))
                .toList();
    }

    private void validateParameters(Task tempTask) {
        if (tempTask.getName().isBlank()) {
            throw new IllegalArgumentException("Task name cannot be blank");
        }
        if (tempTask.getDescription().isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank");
        }

        //todo: Проверка существования пользователя через сервис UserServiceClient
//        if (userServiceClient.getUser(tempTask.getPerformerUserId()) == null) {
//            throw new IllegalArgumentException("Performer user does not exist");
//        }

        //todo: Проверка существования пользователя через сервис UserServiceClient
//        if (userServiceClient.getUser(tempTask.getReporterUserId()) == null) {
//            throw new IllegalArgumentException("Reporter user does not exist");
//        }

        if (!projectRepository.existsById(tempTask.getProject().getId())) {
            throw new IllegalArgumentException("Project does not exist");
        }
    }


}
