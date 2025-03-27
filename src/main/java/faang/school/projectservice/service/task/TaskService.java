package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto getTaskById(long taskId) {
        //todo
        // тут будет логика обращения к репозиторию за данными и возвращать TaskDto, используя MapStruct
        // надо подумать над другим Exception
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Задача не найдена."));
        return taskMapper.taskToTaskDto(task);
    }
}
