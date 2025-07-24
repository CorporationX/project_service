package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.projectservice.util.project.TaskUtil.isInTeam;

@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;
    private final UserContext userContext;

    @Override
    public TaskViewDto createTask(TaskCreateDto createDto) {
        Long projectId = createDto.projectId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(projectId)));

        boolean isUserInProjectTeam = isInTeam(project, userContext.getUserId());
        if (!isUserInProjectTeam) {
            throw new ForbiddenException("Пользователь не состоит в команде проекта");
        }

        Task task = mapper.toEntity(createDto);
        task.setCreatedAt(LocalDateTime.now());

        Task createdTask = taskRepository.save(task);
        return mapper.toViewDto(createdTask);
    }

    @Override
    public TaskViewDto updateTask(TaskUpdateDto updateDto) {
        Long projectId = updateDto.projectId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(projectId)));

        boolean isUserInProjectTeam = isInTeam(project, userContext.getUserId());
        if (!isUserInProjectTeam) {
            throw new ForbiddenException("Пользователь не состоит в команде проекта");
        }

        Task task = mapper.toEntity(updateDto);
        LocalDateTime updateTime = LocalDateTime.now();
        task.setUpdatedAt(updateTime);

        Task updatedTask = taskRepository.save(task);
        log.info("Пользователь id = {} изменил задачу id = {}. Дата: {}.",
                userContext.getUserId(), task.getId(), updateTime);
        return mapper.toViewDto(updatedTask);
    }

    @Override
    public List<TaskViewDto> getByFilter() {
        return List.of();
    }

    @Override
    public TaskViewDto getById() {
        return null;
    }
}
