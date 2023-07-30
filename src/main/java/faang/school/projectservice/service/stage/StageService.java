package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final TaskRepository taskRepository;

    @Transactional
    public StageDto create(StageDto stageDto) {
        validateStageProject(stageDto);

        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }

    @Transactional
    public void deleteStageWithTasks(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        taskRepository.deleteAll(tasks);
        stageRepository.delete(stage);
    }

    @Transactional
    public void deleteStageCloseTasks(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        tasks.forEach(task -> {
            task.setStatus(TaskStatus.CLOSED);
            taskRepository.save(task);
        });

        stageRepository.delete(stage);
    }

    @Transactional
    public void deleteStageTransferTasks(long stageId, long stageToUpdateId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();
        Stage stageToUpdate = stageRepository.getById(stageToUpdateId);
        List<Task> updatedTasks = stageToUpdate.getTasks();

        if (updatedTasks == null) {
            updatedTasks = new ArrayList<>();
        }

        updatedTasks.addAll(tasks);
        stageToUpdate.setTasks(updatedTasks);

        stageRepository.save(stageToUpdate);
        stageRepository.delete(stage);
    }

    public List<StageDto> getAllProjectStages(long projectId) {
        List<Stage> stages = projectRepository.getProjectById(projectId).getStages();

        return stages.stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(long stageId) {
        Stage stage = stageRepository.getById(stageId);

        return stageMapper.toDto(stage);
    }

    private void validateStageProject(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        ProjectStatus projectStatus = project.getStatus();

        if (!projectStatus.equals(ProjectStatus.IN_PROGRESS) && !projectStatus.equals(ProjectStatus.CREATED)) {
            String errorMessage = String.format(
                    "Project %d is %s", project.getId(), projectStatus.name().toLowerCase());

            throw new DataValidationException(errorMessage);
        }
    }
}
