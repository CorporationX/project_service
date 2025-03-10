package faang.school.projectservice.service;

import faang.school.projectservice.event.TaskCompletedEvent;
import faang.school.projectservice.config.feign.UserContext;
import faang.school.projectservice.dto.task.TaskCreateDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskReadDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.task.TaskFilter;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.TaskEventPublisher;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;
    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final UserContext userContext;
    private final List<TaskFilter> taskFilters;

    public TaskReadDto create(TaskCreateDto createDto) {
        verifyUserProjectMembership(createDto.getProjectId());
        Task newTask = taskMapper.toEntity(createDto);
        newTask = taskRepository.save(newTask);
        return taskMapper.toDto(newTask);
    }

    public TaskReadDto update(TaskUpdateDto updateDto) {
        Task task = getTaskById(updateDto.getId());
        verifyUserProjectMembership(task.getProject().getId());
        taskMapper.updateEntityFromDto(task, updateDto);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public List<TaskReadDto> getAllFilteredTasksByProjectId(long projectId, TaskFilterDto filterDto) {
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);

        return tasks.stream()
                .filter(subProject -> taskFilters.stream().filter(filter -> filter.isApplicable(filterDto))
                        .anyMatch(filter -> filter.filterEntity(subProject, filterDto)))
                .map(taskMapper::toDto)
                .toList();
    }

    public List<TaskReadDto> getAllTasksByProjectId(long projectId) {
        verifyUserProjectMembership(projectId);
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);

        return tasks.stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public void delete(long taskId) {
        Task task = taskRepository.getReferenceById(taskId);
        verifyUserProjectMembership(task.getProject().getId());
        taskRepository.delete(task);
    }

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

    private void verifyUserProjectMembership(long projectId) {
        Project project = projectService.getProjectById(projectId);
        long currentUserId = userContext.getUserId();

        List<Long> membersIdList = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .toList();

        if (!membersIdList.contains(currentUserId)) {
            throw new BusinessException("Невозможно создать/изменить задачу пользователю, который не является участником проекта");
        }
    }

    public Task getTaskById(long taskId) {
        Task task = taskRepository.getReferenceById(taskId);
        verifyUserProjectMembership(task.getProject().getId());
        return task;
    }

}
