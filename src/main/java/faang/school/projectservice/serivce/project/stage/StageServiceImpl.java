package faang.school.projectservice.serivce.project.stage;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.dto.project.stage.StageUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.project.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.serivce.project.stage.filters.StageFilter;
import faang.school.projectservice.serivce.project.stage.remove.RemoveStrategyExecutor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static faang.school.projectservice.exception.ExceptionMessages.EXECUTOR_ROLE_NOT_VALID;
import static faang.school.projectservice.exception.ExceptionMessages.PROJECT_NOT_FOUND;
import static faang.school.projectservice.exception.ExceptionMessages.WRONG_PROJECT_STATUS;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TaskRepository taskRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;
    private final List<RemoveStrategyExecutor> removeStrategies;
    private final Map<RemoveStrategy, RemoveStrategyExecutor> removeStrategyExecutors;

    @PostConstruct
    private void fillRemoveStrategyExecutors() {
        for (RemoveStrategyExecutor strategyExecutor : removeStrategies) {
            removeStrategyExecutors.put(strategyExecutor.getStrategyType(), strategyExecutor);
        }
    }

    @Override
    public StageDto createStage(StageCreateDto stageCreateDto) {
        validateProject(stageCreateDto.projectId());
        Project project = projectRepository.getProjectById(stageCreateDto.projectId());
        if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
            throw new DataValidationException(
                    WRONG_PROJECT_STATUS.getMessage()
                            .formatted(stageCreateDto.projectId()));
        }
        Stage newStage = stageMapper.toStage(stageCreateDto);
        newStage.getStageRoles().forEach(role -> role.setStage(newStage));
        return stageMapper.toStageDto(stageRepository.save(newStage));
    }

    @Override
    public List<StageDto> getStages(Long projectId, StageFilterDto filters) {
        validateProject(projectId);
        List<Stage> stages = projectRepository.getProjectById(projectId).getStages();
        List<Stage> filteredStages = filterStages(stages.stream(), filters);
        return stageMapper.toStageDtos(filteredStages);
    }

    @Override
    public StageDto removeStage(Long stageId, RemoveTypeDto removeTypeDto) {
        Stage stage = stageRepository.getById(stageId);
        removeStrategyExecutors.get(removeTypeDto.removeStrategy()).execute(stage, removeTypeDto);
        return stageMapper.toStageDto(stage);
    }

    @Override
    public StageDto updateStage(StageUpdateDto stageUpdateDto, Long userId, Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        if (stageUpdateDto.stageName() != null) {
            stage.setStageName(stageUpdateDto.stageName());
        }
        List<TeamMember> executors = teamMemberRepository.findAllByIds(stageUpdateDto.executorIds());
        validateExecutorsRoles(stage, executors);
        stage.setExecutors(executors);
        checkMemberCountForRole(stage, executors, userId);
        return stageMapper.toStageDto(stageRepository.save(stage));
    }

    @Override
    public StageDto getStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        return stageMapper.toStageDto(stage);
    }

    private List<Stage> filterStages(Stream<Stage> stages, StageFilterDto filters) {
        for (StageFilter filter : stageFilters) {
            if (filter.isApplicable(filters)) {
                filter.apply(stages, filters);
            }
        }
        return stages.toList();
    }

    private void validateExecutorsRoles(Stage stage, List<TeamMember> executors) {
        List<TeamRole> roles = stage.getStageRoles().stream()
                .map(StageRoles::getTeamRole)
                .toList();
        for (TeamMember executor : executors) {
            if (executor.getRoles().stream()
                    .noneMatch(roles::contains)) {
                throw new DataValidationException(EXECUTOR_ROLE_NOT_VALID.getMessage()
                        .formatted(executor.getId(), stage.getStageId()));
            }
        }
    }

    private void checkMemberCountForRole(Stage stage, List<TeamMember> executors, Long userId) {
        Map<TeamRole, Long> neededRolesCount = new HashMap<>();
        for (StageRoles role : stage.getStageRoles()) {
            long executorWithNeededRoleCount = executors.stream().
                    filter(executor -> executor.getRoles().contains(role.getTeamRole()))
                    .count();
            long neededCount = role.getCount() - executorWithNeededRoleCount;
            if (neededCount > 0) {
                neededRolesCount.put(role.getTeamRole(), neededCount);
            }
        }
        for (Map.Entry<TeamRole, Long> entry : neededRolesCount.entrySet()) {
            projectRepository.getProjectById(stage.getProject().getId()).getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(teamMember -> !executors.contains(teamMember))
                    .filter(teamMember -> teamMember.getRoles().contains(entry.getKey()))
                    .limit(entry.getValue())
                    .forEach(teamMember -> sendInvitation(stage, teamMember, userId));
        }
    }

    private void sendInvitation(Stage stage, TeamMember executor, Long userId) {
//        stageInvitationServiceImpl.sendInvitation(StageInvitationDto.builder()
//                .stageId(stage.getStageId())
//                .authorId(userId)
//                .invitedId(executor.getId())
//                .build());
//        что-то типо такого
    }

    private void validateProject(Long projectId) {
        if (projectRepository.existsById(projectId) == false) {
            throw new EntityNotFoundException(PROJECT_NOT_FOUND.getMessage().formatted(projectId));
        }
    }
}
