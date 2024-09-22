package faang.school.projectservice.service.task;

import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {
    private final StageRepository stageRepository;

    public void deleteAllTasksByStageId(Long stageId) {
        stageRepository.deleteAllTasksByStageId(stageId);
    }
}
