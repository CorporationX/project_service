package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;

    @Transactional
    public StageDto create(StageDto stageDto) {
        validate(stageDto);

        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }

    private void validate(StageDto stageDto) {
        validateStageProjectIsValid(stageDto);
    }

    private void validateStageProjectIsValid(StageDto stageDto) {
        Project project = getStageProject(stageDto);
        ProjectStatus projectStatus = project.getStatus();

        if (!projectStatus.equals(ProjectStatus.IN_PROGRESS) && !projectStatus.equals(ProjectStatus.CREATED)) {
            String errorMessage = String.format(
                    "Project %d is %s", project.getId(), projectStatus.name().toLowerCase());

            throw new DataValidationException(errorMessage);
        }
    }

    private Project getStageProject(StageDto stageDto) {
        try {
            return projectRepository.getProjectById(stageDto.getProjectId());
        } catch (IllegalArgumentException e) {
            throw new DataValidationException("Project does not exist");
        }
    }
}
