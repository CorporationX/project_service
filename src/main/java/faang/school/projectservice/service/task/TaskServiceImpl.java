package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.FilterService;
import faang.school.projectservice.service.filter.task.FilterServiceImplTask;
import faang.school.projectservice.util.project.TaskUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final FilterServiceImplTask filterService;
    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final UserContext userContext;
    private final TaskUtil taskUtil;

    @Override
    @Transactional
    public TaskViewDto createTask(TaskCreateDto createDto) {
        Long projectId = createDto.projectId();

        taskUtil.isInTeam(projectId);

        Task task = mapper.toEntity(createDto);
        task.setCreatedAt(LocalDateTime.now());

        Task createdTask = repository.save(task);
        return mapper.toViewDto(createdTask);
    }

    @Override
    @Transactional
    public TaskViewDto updateTask(long id, TaskUpdateDto updateDto) {
        Long projectId = updateDto.projectId();
        taskUtil.isInTeam(projectId);

        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
         mapper.update(updateDto, task);

        LocalDateTime updateTime = LocalDateTime.now();
        task.setUpdatedAt(updateTime);

        Task updatedTask = repository.save(task);
        log.info("Пользователь id = {} изменил задачу id = {}. Дата: {}.",
                userContext.getUserId(), task.getId(), updateTime);
        return mapper.toViewDto(updatedTask);
    }

    @Override
    @Transactional
    public List<TaskViewDto> getByFilter(TaskFilterDto taskFilterDto) {
        List<Task> tasks = repository.findAll();
        List<Task> filteredTasks = filterService.getFilteredList(tasks, taskFilterDto);

        log.info("Получение списка всех задач проекта с фильтрами.");
        return filteredTasks.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    @Transactional
    public TaskViewDto getById(long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));

        Long projectId = task.getProject().getId();
        taskUtil.isInTeam(projectId);

        return mapper.toViewDto(task);
    }
}
