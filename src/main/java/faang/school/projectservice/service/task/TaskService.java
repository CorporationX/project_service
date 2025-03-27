package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    //TODO: "1. Создание задачи. Задачи могут создавать все участники проекта.
    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.taskDtoToTask(taskDto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.taskToTaskDto(savedTask);
    }


    //TODO: "2. Изменение задачи (описание, статус, deadline, исполнитель, изменение родительской задачи,
    // изменение связанных задач). Изменение могут делать все участники,
    // важно логировать для аудита дату изменения и пользователя, который изменил данные.
    @Transactional
    public TaskDto updateTask(Long taskId, TaskDto taskDto, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена."));
        Task updatedTask = taskMapper.taskDtoToTask(taskDto);
        updatedTask.setId(task.getId());
        updatedTask.setCreatedAt(task.getCreatedAt());
        updatedTask.setUpdatedAt(LocalDateTime.now());
        updatedTask.setReporterUserId(userId);

        Task savedTask = taskRepository.save(updatedTask);
        return taskMapper.taskToTaskDto(savedTask);
    }

    //TODO: "3. Получить все задачи проекта с фильтрами по статусу, исполнителю или ключевому слову.
    public List<TaskDto> getFilteredTasks(Long projectId, TaskStatus status, Long performerId, String keyword, Long userId) {
        return null;
    }

    //TODO: "4. Получить все задачи проекта."!!! пока не могу понять как сделать с валидацией
    // пользователя. Не вижу взаимосвязи в таблицах.
    @Transactional
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
        // надо подумать над другим Exception наверное
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена."));
        return taskMapper.taskToTaskDto(task);
    }

}
