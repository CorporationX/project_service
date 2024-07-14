package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> findAllByStage(Stage stage) {
        return taskRepository.findAllByStage(stage);
    }
}
