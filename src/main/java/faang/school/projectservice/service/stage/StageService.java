package faang.school.projectservice.service.stage;

import faang.school.projectservice.deletestrategy.DeleteStrategyRegistry;
import faang.school.projectservice.dto.stage.DeleteTypeDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.exception.ExecutorRoleNotValidException;
import faang.school.projectservice.exception.NotEnoughTeamMembersException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.WrongProjectStatusException;
import faang.school.projectservice.filters.*;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;
    private final DeleteStrategyRegistry deleteStrategyRegistry;

    public StageDto createStage(StageCreateDto stageCreateDto) {
        validateProject(stageCreateDto.projectId());
        Project project = projectRepository.getProjectById(stageCreateDto.projectId());
        if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
            throw new WrongProjectStatusException(stageCreateDto.projectId());
        }
        Stage newStage = stageMapper.toEntity(stageCreateDto);
        newStage.getStageRoles().forEach(role -> role.setStage(newStage));
        Stage savedStage = stageRepository.save(newStage);
        log.info("Stage created: {}", savedStage.getStageId());
        return stageMapper.toDto(savedStage);
    }

    public List<StageDto> getStages(Long projectId, StageFilterDto filter) {
        validateProject(projectId);
        List<Stage> stages = projectRepository.getProjectById(projectId).getStages();
        List<Stage> filteredStages = filterStages(stages.stream(), filter);
        log.info("Found {} filtered stages for project: {}", filteredStages.size(), projectId);
        return stageMapper.toDtos(filteredStages);
    }

    public StageDto deleteStage(Long stageId, DeleteTypeDto deleteTypeDto) {
        Stage stage = stageRepository.getById(stageId);
        deleteStrategyRegistry.getExecutor(deleteTypeDto.getDeleteStrategy()).execute(stage, deleteTypeDto);
        log.info("Stage removed: {}", stageId);
        return stageMapper.toDto(stage);
    }

    public StageDto updateStage(StageUpdateDto stageUpdateDto, Long stageId) {
        log.info("Updating stage with ID: {}", stageId);
        Stage stage = stageRepository.getById(stageId);
        if (stageUpdateDto.stageName() != null) {
            stage.setStageName(stageUpdateDto.stageName());
            log.info("Stage name updated: {}", stageId);
        }
        List<TeamMember> executors = teamMemberRepository.findAllByIds(stageUpdateDto.executorIds());
        validateExecutorsRoles(stage, executors);
        stage.setExecutors(executors);
        log.info("New executors set for stage: {}", stageId);
        setStageForExecutors(stage, executors);
        checkMemberCountForRole(stage, executors);
        log.info("Stage '{}' updated successfully", stage.getStageName());
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public StageDto getStage(Long id) {
        return stageMapper.toDto(stageRepository.getById(id));
    }

    private void checkMemberCountForRole(Stage stage, List<TeamMember> executors) {
        Map<TeamRole, Long> executorRolesCount = executors.stream()
                .flatMap(executor -> executor.getRoles().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<TeamRole, Long> neededRolesCount = stage.getStageRoles().stream()
                .filter(stageRole ->
                        stageRole.getCount() - executorRolesCount.getOrDefault(stageRole.getTeamRole(), 0L) > 0)
                .collect(Collectors.toMap(
                        StageRoles::getTeamRole,
                        stageRole ->
                                stageRole.getCount() - executorRolesCount.getOrDefault(stageRole.getTeamRole(), 0L)));
        List<TeamMember> projectTeamMembers = getTeamMembersForProjectExcludingExecutors(stage.getProject(), executors);
        for (Map.Entry<TeamRole, Long> entry : neededRolesCount.entrySet()) {
            List<TeamMember> foundedTeamMembers = projectTeamMembers.stream()
                    .filter(teamMember -> teamMember.getRoles().contains(entry.getKey()))
                    .limit(entry.getValue())
                    .toList();
            if (foundedTeamMembers.size() < entry.getValue()) {
                throw new NotEnoughTeamMembersException(entry.getKey(), stage.getProject().getId());
            }
//            foundedTeamMembers.forEach(teamMember -> sendInvitationsIfNeeded(stage));
        }
    }

    private List<TeamMember> getTeamMembersForProjectExcludingExecutors(Project project, List<TeamMember> executors) {
        return project.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> !executors.contains(teamMember))
                .toList();
    }

    private void validateExecutorsRoles(Stage stage, List<TeamMember> executors) {
        List<TeamRole> roles = stage.getStageRoles().stream()
                .map(StageRoles::getTeamRole)
                .toList();
        for (TeamMember executor : executors) {
            if (executor.getRoles().stream()
                    .noneMatch(roles::contains)) {
                throw new ExecutorRoleNotValidException(executor.getId(), stage.getStageId());
            }
        }
    }

    private void setStageForExecutors(Stage stage, List<TeamMember> executors) {
        for (TeamMember executor : executors) {
            executor.getStages().add(stage);
        }
    }

    private List<Stage> filterStages(Stream<Stage> stages, StageFilterDto filters) {
        for (StageFilter filter : stageFilters) {
            if (filter.isApplicable(filters)) {
                filter.apply(stages, filters);
            }
        }
        return stages.toList();
    }

    private void validateProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }
    }
}