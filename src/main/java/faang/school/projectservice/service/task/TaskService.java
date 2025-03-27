package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    //TODO: "1. Создание задачи. Задачи могут создавать все участники проекта.
    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.taskDtoToTask(taskDto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.taskToTaskDto(savedTask);
    }

    //TODO: "4. Получить все задачи проекта."!!! пока не могу понять как сделать с валидацией
    // пользователя. Не вижу взаимосвязи в таблицах.
    public List<TaskDto> getAllTasksByProjectId(long projectId) {
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        return tasks.stream()
                .map(taskMapper::taskToTaskDto)
                .toList();
    }

    //TODO: "5. Получить задачу по id"
    @Transactional
    public TaskDto getTaskById(long taskId) {
        //todo
        // тут будет логика обращения к репозиторию за данными и возвращать TaskDto, используя MapStruct
        // надо подумать над другим Exception
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Задача не найдена."));
        return taskMapper.taskToTaskDto(task);
    }
}
