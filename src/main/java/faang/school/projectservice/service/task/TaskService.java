package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.task.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskMapper taskMapper;
    private final TaskValidator taskValidator;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskDto createTask(TaskDto taskDto) {
        taskValidator.validateTeamMember(taskDto.getReporterUserId(), taskDto.getProjectId());
        taskValidator.validateTeamMember(taskDto.getPerformerUserId(), taskDto.getProjectId());
        Task task = taskMapper.toEntity(taskDto);
        task.setProject(projectRepository.getProjectById(taskDto.getProjectId()));
        if(taskDto.getParentTaskId() != null) {
            task.setParentTask(taskValidator.validateTask(taskDto.getParentTaskId()));
        }
        return taskMapper.toDto(taskRepository.save(task));
    }

    public TaskDto updateTask(Long taskId, TaskDto taskDto) {
        Task task = taskValidator.validateTask(taskId);
        updateFields(task, taskDto);
        return taskMapper.toDto(taskRepository.save(task));
    }

    private void updateFields(Task task, TaskDto taskDto) {
        if(!taskValidator.descriptionIsNull(taskDto.getDescription())) {
            task.setDescription(taskDto.getDescription());
        }
        if(!taskValidator.statusIsNull(taskDto.getStatus())) {
            task.setStatus(TaskStatus.valueOf(taskDto.getStatus()));
        }
        if(!taskValidator.performerUserIdIsNull(taskDto.getPerformerUserId())) {
            task.setPerformerUserId(taskDto.getPerformerUserId());
        }
        if(!taskValidator.parentTaskIdIsNull(taskDto.getParentTaskId())) {
            Task parentTask = taskValidator.validateTask(taskDto.getParentTaskId());
            task.setParentTask(parentTask);
        }
        if(!taskValidator.linkedTasksIdsIsNull(taskDto.getLinkedTasksIds())) {
            List<Task> linkedTasks = taskDto.getLinkedTasksIds().stream()
                    .map(taskValidator::validateTask)
                    .collect(Collectors.toList());
            task.setLinkedTasks(linkedTasks);
        }
    }
}
