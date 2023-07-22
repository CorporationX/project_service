package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRolesRepository stageRolesRepository;
    private final StageMapper stageMapper;

    public StageDto create(StageDto stageDto) {
        validate(stageDto);

        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }

    private void validate(StageDto stageDto) {
        validateStageExecutors(stageDto);
        validateStageProjectIsValid(stageDto);
    }

    private void validateStageProjectIsValid(StageDto stageDto) {
        Project project = getStageProject(stageDto);
        ProjectStatus projectStatus = project.getStatus();

        if (projectStatus.equals(ProjectStatus.CANCELLED) || projectStatus.equals(ProjectStatus.COMPLETED)) {
            String errorMessage = String.format("Project %d is %s", project.getId(), projectStatus.name().toLowerCase());

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

    private void validateStageExecutors(StageDto stageDto) {
        List<Long> executors =  stageDto.getExecutorIds();
        List<Long> roles =  stageDto.getStageRoleIds();
        List<TeamRole> teamRoles = TeamRole.getAll();

    }

}
