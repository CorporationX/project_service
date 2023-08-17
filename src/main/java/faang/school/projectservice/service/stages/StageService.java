package faang.school.projectservice.service.stages;

import faang.school.projectservice.dto.stages.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stages.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;

    public StageDto create(StageDto stageDto) {
        checkStageNotExist(stageDto);
        Stage stage = stageMapper.toEntity(stageDto);
        stage.setStageName(stageDto.getStageName());
        stage.setProject(stageDto.getProject());
        validatePrivateProject(stageDto.getStageId());
        stageRepository.save(stage);
        return stageMapper.toDto(stage);
    }

    private void validatePrivateProject(long projectId) {
        Project currentProject = projectRepository.getProjectById(projectId);
        boolean isProjectPrivate = currentProject.getVisibility().equals(ProjectVisibility.PRIVATE);
        if (isProjectPrivate){
            throw new DataValidationException(String.format("Project with id %s is private", currentProject.getId()));
        }
    }

    private void checkStageNotExist(StageDto stageDto){
        if (stageDto.getStageId() == null){
            throw new DataValidationException(String.format("Stage %s is already exest", stageDto.getStageId()));
        }
    }
}
