package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final UserContext userContext;

    @Override
    public TaskViewDto createTask(TaskCreateDto createDto) {

        Task taskEntity = mapper.toEntity(createDto);

        //Добавить проверку на принадлежность пользователя к проекту
        Optional<Task> project = repository.findById(createDto.projectId());

        Task createdTask = repository.save(mapper.toEntity(createDto));
        return mapper.toViewDto(createdTask);
    }

    @Override
    public TaskViewDto updateTask(TaskUpdateDto updateDto) {
        return null;
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
