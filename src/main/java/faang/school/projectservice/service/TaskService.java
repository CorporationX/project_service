package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<Task> findAllByStage(Stage stage) {
        return taskRepository.findAllByStage(stage);
    }
}
