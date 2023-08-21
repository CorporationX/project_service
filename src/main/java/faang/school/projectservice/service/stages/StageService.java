package faang.school.projectservice.service.stages;

import faang.school.projectservice.dto.stages.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ProjectException;
import faang.school.projectservice.exception.StageException;
import faang.school.projectservice.mapper.stages.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;

    public StageDto create(StageDto stageDto) {
        Stage stage = stageRepository.save(stageMapper.toEntity(validStage(stageDto)));
        return stageMapper.toDto(stage);
    }

    private StageDto validStage(StageDto stage) {
        if (stage.getStageId() == null) {
            throw new StageException("Invalid ID");
        }
        if (stage.getStageName().isBlank()) {
            throw new StageException("Name cannot be empty");
        }

        Project project = projectRepository.getProjectById(stage.getProject().getId());
        boolean projectInProgress = project.getStatus().equals(ProjectStatus.IN_PROGRESS);

        if (!projectInProgress) {
            throw new ProjectException(MessageFormat.format("Project with id {0} unavailable", project.getId()));
        }

        return stageMapper.toDto(stageRepository.getById(stage.getStageId()));
    }

}
