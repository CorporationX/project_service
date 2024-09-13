package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.taskActionAfterDeletingStage.TaskActionAfterDeletingStage;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final StageRolesService stageRolesService;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.projectId());
        if (project == null) {
            throw new IllegalArgumentException("Project not found by id: " + stageDto.projectId());
        }
        Stage stage = stageMapper.toEntity(stageDto);

        stage.setProject(project);

        Stage savedStage = stageRepository.save(stage);
        List<StageRoles> createdStageRoles = stageRolesService.createStageRolesForStageById(
                savedStage.getStageId(), stageDto.rolesWithAmount());

        savedStage.setStageRoles(createdStageRoles);

        return stageMapper.toDto(savedStage);
    }


    public List<StageDto> getFilteredStagesByRolesAndStatus(
            Long projectId,
            List<TeamRole> roles,
            List<TaskStatus> taskStatuses) {

        return stageMapper.toDtos(
                stageRepository.findStagesByProjectAndFilters(
                        projectId, roles, taskStatuses)
        );
    }

    @Transactional
    public void deleteStage(Long stageId, TaskActionAfterDeletingStage taskAction) {

    }


    public List<StageDto> getAllStagesByProjectId(Long projectId) {
        List<Stage> stagesDto = stageRepository.findAllStagesByProjectId(projectId);
        return stageMapper.toDtos(stagesDto);
    }

    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}
