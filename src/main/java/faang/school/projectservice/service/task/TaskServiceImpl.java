package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.FilterService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static faang.school.projectservice.util.project.ProjectUtil.isInTeam;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final FilterService<Task, TaskFilterDto> filterService;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper mapper;
    private final UserContext userContext;
    private final StageRepository stageRepository;

    @Override
    @Transactional
    public TaskViewDto createTask(TaskCreateDto createDto) {
        Task task = mapper.toEntity(createDto);
        //task.setCreatedAt(LocalDateTime.now());

        Long projectId = createDto.projectId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(projectId)));

        isInTeam(projectId, userContext.getUserId(), project);

        Task parentTask = taskRepository.findById(createDto.parentTaskId())
                .orElseThrow(() -> new EntityNotFoundException
                        (String.valueOf(createDto.parentTaskId())));

        List<Task> linkedTask = createDto.linkedTasksId().stream()
                .map(aLong -> taskRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException
                        (String.valueOf(aLong)))).collect(Collectors.toList());

        Stage stage = stageRepository.findById(createDto.stageId())
                .orElseThrow(() -> new EntityNotFoundException
                        (String.valueOf(createDto.stageId())));

        task.setParentTask(parentTask);
        task.setLinkedTasks(linkedTask);
        task.setProject(project);
        task.setStage(stage);

        Task createdTask = taskRepository.save(task);
        return mapper.toViewDto(createdTask);
    }

    @Override
    @Transactional
    public TaskViewDto updateTask(long id, TaskUpdateDto updateDto) {
        Long projectId = updateDto.projectId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(projectId)));
        isInTeam(projectId, userContext.getUserId(), project);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
         mapper.update(updateDto, task);

        LocalDateTime updateTime = LocalDateTime.now();
        task.setUpdatedAt(updateTime);

        Task updatedTask = taskRepository.save(task);
        log.info("Пользователь id = {} изменил задачу id = {}. Дата: {}.",
                userContext.getUserId(), task.getId(), updateTime);
        return mapper.toViewDto(updatedTask);
    }

    @Override
    @Transactional
    public List<TaskViewDto> getByFilter(TaskFilterDto taskFilterDto, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(projectId)));
        isInTeam(projectId, userContext.getUserId(), project);

        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        List<Task> filteredTasks = filterService.getFilteredList(tasks, taskFilterDto);

        log.info("Получение списка всех задач проекта с фильтрами.");
        return filteredTasks.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    @Transactional
    public TaskViewDto getById(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));

        Long projectId = task.getProject().getId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(projectId)));
        isInTeam(projectId, userContext.getUserId(), project);

        return mapper.toViewDto(task);
    }
}
