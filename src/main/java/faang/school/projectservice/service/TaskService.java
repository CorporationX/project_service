package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;

    public List<Task> getAllTasksOfStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        return stage.getTasks();
    }

    public List<Task> cancelTasksOfStage(Long stageId){
        List<Task> tasks = getAllTasksOfStage(stageId);
        if (tasks.isEmpty()){
            throw new DataValidationException("There is no tasks to cancel.");
        }
        tasks.forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        taskRepository.saveAll(tasks);
        return tasks;
    }
}
