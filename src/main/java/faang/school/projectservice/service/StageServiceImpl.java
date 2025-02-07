package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StageServiceImpl implements StageService {

    private static final List<ProjectStatus> INVALID_STATUSES = List.of(ProjectStatus.COMPLETED, ProjectStatus.CANCELLED);

    private final StageRepository stageRepository;
    private final StageRolesRepository stageRolesRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;

    @Override
    public StageDto createStage(StageDto stageDto) {
        return saveValidStage(stageDto);
    }

    @Override
    public StageDto updateStage(StageDto stageDto) {
        if (stageDto.getStageId() == null) {
            throw new DataValidationException("Stage id cannot be null");
        }
        return saveValidStage(stageDto);
    }

    @Override
    public List<StageDto> getAllStagesByFilter(StageFilterDto filter) {
        validateRole(filter);
        validateStatus(filter);
        return getStagesByFilter(filter);
    }

    @Override
    public List<StageDto> getAllStages() {
        return stageRepository.findAll().stream()
                .map(stageMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteStage(long stageId) {
        stageRepository.deleteById(stageId);
        taskRepository.updateStatus(stageId, TaskStatus.DONE.name());
    }

    @Override
    public StageDto getStageById(long stageId) {
        return stageRepository.findById(stageId)
                .map(stageMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Stage with id %d not found", stageId)));
    }

    private void validateRole(StageFilterDto filter) {
        filter.getRole()
                .filter(role -> !role.isEmpty())
                .ifPresent(role -> {
                    if (!isValidateRole(role)) {
                        throw new DataValidationException("Invalid role: " + role);
                    }
                });
    }

    private void validateStatus(StageFilterDto filter) {
        filter.getStatus().filter(status -> !status.isEmpty())
                .ifPresent(status -> {
                    if (!isValidStatus(status)) {
                        throw new DataValidationException("Invalid status: " + status);
                    }
                });
    }

    private StageDto saveValidStage(StageDto stageDto) {
        Project project = getActiveProject(stageDto);
        if (projectRepository.existsByOwnerIdAndName(stageDto.getUserId(), project.getName())) {
            Stage stage = stageMapper.toEntity(stageDto);
            stage.setProject(project);
            stage.setStageRoles(mapStageRoles(stageDto, stage));
            Stage savedStage = stageRepository.save(stage);
            return stageMapper.toDto(savedStage);
        }
        throw new ResourceNotFoundException(String.format("Project with id %d for this user %d not found", stageDto.getProjectId(), stageDto.getUserId()));
    }

    private List<StageRoles> mapStageRoles(StageDto stageDto, Stage stage) {
        return stageDto.getStageRoles().stream()
                .map(stageRolesMapper::toEntity)
                .peek(stageRole -> stageRole.setStage(stage))
                .toList();
    }

    private boolean isValidStatus(String status) {
        return Arrays.stream(TaskStatus.values())
                .anyMatch(enumValue -> enumValue.name().equals(status.toUpperCase()));
    }

    private boolean isValidateRole(String role) {
        return Arrays.stream(TeamRole.values())
                .anyMatch(enumValue -> enumValue.name().equals(role.toUpperCase()));
    }

    private List<StageRoles> getStageRolesByIds(StageDto stageDto) {
        return stageDto.getStageRoles().stream()
                .map(stageRolesDto -> stageRolesDto.getId())
                .map(stageRolesId -> stageRolesRepository.findById(stageRolesId)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("StageRoles with id %d not found", stageRolesId))))
                .toList();
    }

    private Project getActiveProject(StageDto stageDto) {
        Project project = projectRepository.findById(stageDto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Project with id %d not found", stageDto.getProjectId())));
        if (INVALID_STATUSES.contains(project.getStatus())) {
            throw new DataValidationException(String.format("Project with id %d is in an invalid status:%s", stageDto.getProjectId(), project.getStatus()));
        }
        return project;
    }

    private List<StageDto> getStagesByFilter(StageFilterDto filter) {
        return stageRepository.findAll().stream()
                .filter(stage -> isRoleMatching(stage, filter.getRole())
                        && isStatusMatching(stage, filter.getStatus()))
                .map(stageMapper::toDto)
                .toList();
    }

    private boolean isRoleMatching(Stage stage, Optional<String> role) {
        return role
                .filter(r -> !r.isEmpty())
                .map(r -> stage.getStageRoles().stream()
                        .anyMatch(stageRole -> stageRole.getTeamRole().name().equalsIgnoreCase(r)))
                .orElse(true);
    }

    private boolean isStatusMatching(Stage stage, Optional<String> status) {
        return status
                .filter(s -> !s.isEmpty())
                .map(s -> stage.getTasks().stream()
                        .anyMatch(task -> task.getStatus().name().equalsIgnoreCase(s)))
                .orElse(true);
    }
}