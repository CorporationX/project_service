package faang.school.projectservice.service;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public List<TaskDto> cancelTasks (Long stageId) {
        var tasks = getStageTasks(stageId)
                .stream()
                .map(taskMapper::toEntity)
                .toList();

        tasks.forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        taskRepository.saveAll(tasks);

        return tasks.stream().map(taskMapper::toDto).toList();
    }

    public List<TaskDto> getStageTasks (Long stageId) {
        var stage = stageRepository.getById(stageId);
        var tasks = stage.getTasks();

        return tasks.stream().map(taskMapper::toDto).toList();
    }
}
