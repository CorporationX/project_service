package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;


@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageRepository.save(stageMapper.toStage(validStage(stageDto)));
        return stageMapper.toStageDto(stage);
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
        boolean projectCreated = project.getStatus().equals(ProjectStatus.CREATED);

        if (projectInProgress || projectCreated) {
            return stageMapper.toStageDto(stageRepository.getById(stage.getStageId()));
        } else {
            throw new ProjectException(MessageFormat.format("Project with id {0} unavailable", project.getId()));
        }
    }
}
