package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final StageMapper stageMapper;


    public StageDto createStage(StageDto stageDto) {
        validationStageDto(stageDto);
        Stage saved = stageRepository.save(stageMapper.toEntity(stageDto));
        return stageMapper.toDto(saved);
    }

    public void deleteStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        taskService.cancelTasksOfStage(stageId);
        stageRepository.delete(stage);
    }

    public StageDto getStageById(Long stageId) {
        Stage stageById = stageRepository.getById(stageId);
        return stageMapper.toDto(stageById);
    }

    public List<StageDto> findAllStagesOfProject(Long projectId) {
        return projectRepository.getProjectById(projectId)
                .getStages()
                .stream()
                .map(stageMapper::toDto)
                .toList();
    }

    private void validationStageDto(StageDto stageDto) {
        Project projectById = projectRepository.getProjectById(stageDto.getProjectId());

        if (projectById.getStatus().equals(ProjectStatus.COMPLETED) ||
                projectById.getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("You cannot create a stage in a closed or canceled project");
        }
    }
}
