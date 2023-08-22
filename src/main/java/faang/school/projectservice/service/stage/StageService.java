package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final StageRolesRepository stageRolesRepository;

    private final StageMapper stageMapper;

    private final StageServiceValidator stageServiceValidator;

    public StageDto create(StageDto stageDto) {
        if (!isProjectActive(stageDto)) {
            throw new DataValidationException("Project is not active");
        }
        checkUnnecessaryExecutorsExist(stageDto);
        Stage stage = save(stageDto);
        log.info("Created stage: {}", stage);
        return stageMapper.toDto(stage);
    }

    public List<StageDto> getStagesByProjectId(Long projectId) {
        List<Stage> stages = projectRepository
                .getProjectById(projectId)
                .getStages();
        log.info("Stages retrieved for project: {}", projectId);
        return stageMapper.toDtoList(stages);
    }


    private Stage save(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage.setStageRoles(getStageRoles(stageDto));
        stage.setExecutors(getExecutors(stageDto));
        stage.setProject(getProject(stageDto));
        return stageRepository.save(stage);
    }

    private void checkUnnecessaryExecutorsExist(StageDto stageDto) {
        List<DataValidationException> errors = stageServiceValidator.getUnnecessaryExecutorsExist(
            getExecutors(stageDto),
            getStageRoles(stageDto)
        );
        if (!errors.isEmpty()) {
            String errorsMessage = buildErrorsMessage(errors);
            throw new DataValidationException(errorsMessage);
        }
    }

    private String buildErrorsMessage(List<DataValidationException> errors) {
        StringBuilder builder = new StringBuilder();
        for (DataValidationException exception : errors) {
            builder
                    .append(exception.getMessage())
                    .append("\n");
        }
        return builder.toString();
    }

    private Project getProject(StageDto stageDto) {
        return projectRepository.getProjectById(stageDto.getStageId());
    }

    private List<StageRoles> getStageRoles(StageDto stageDto) {
        return stageRolesRepository.findAllById(stageDto.getStageRoleIds());
    }

    private List<TeamMember> getExecutors(StageDto stageDto) {
        return teamMemberJpaRepository.findAllById(stageDto.getTeamMemberIds());
    }

    private boolean isProjectActive(StageDto stageDto) {
        return projectRepository
                .getProjectById(stageDto.getProjectId())
                .isProjectStatusActive();
    }

    private boolean isProjectActive(Stage stage) {
        return stage.getProject().isProjectStatusActive();
    }
}
