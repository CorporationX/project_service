package faang.school.projectservice.service.stages;

import faang.school.projectservice.dto.stages.StageDto;
import faang.school.projectservice.exception.ProjectException;
import faang.school.projectservice.exception.StageException;
import faang.school.projectservice.mapper.stages.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        Stage stage = stageRepository.save(stageMapper.toEntity(validateStage(stageDto)));
        return stageMapper.toDto(stage);
    }

    @NotBlank
    @NotNull
    private StageDto validateStage(StageDto stage) {
        Project project = projectRepository.getProjectById(stage.getProject().getId());
        boolean projectInProgress = project.getStatus() == ProjectStatus.IN_PROGRESS;

        if (!projectInProgress) {
            throw new ProjectException(MessageFormat.format("Project with id {0} unavailable", project.getId()));
        }

        if (stage.getStageName().isEmpty()){
            throw new StageException("Stage name cannot be empty");
        }

        return stageMapper.toDto(stageRepository.getById(stage.getStageId()));
    }

}
