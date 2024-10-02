package faang.school.projectservice.service.task;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> getAllById (List<Long> taskIds){
        return Optional.of(taskRepository.findAllById(taskIds))
                .orElseThrow(EntityNotFoundException::new);
    }
}
