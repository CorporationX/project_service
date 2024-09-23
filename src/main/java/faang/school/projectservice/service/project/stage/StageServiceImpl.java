package faang.school.projectservice.service.project.stage;

import faang.school.projectservice.dto.project.stage.RemoveStrategy;
import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.dto.project.stage.StageUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
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
import faang.school.projectservice.service.project.stage.filters.StageFilter;
import faang.school.projectservice.service.project.stage.removestrategy.RemoveStrategyExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.projectservice.exception.ExceptionMessages.EXECUTOR_ROLE_NOT_VALID;
import static faang.school.projectservice.exception.ExceptionMessages.NOT_ENOUGH_TEAM_MEMBERS_IN_PROJECT;
import static faang.school.projectservice.exception.ExceptionMessages.PROJECT_NOT_FOUND;
import static faang.school.projectservice.exception.ExceptionMessages.WRONG_PROJECT_STATUS;

@Service
@Slf4j
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;
    private final Map<RemoveStrategy, RemoveStrategyExecutor> removeStrategyExecutors;

    public StageServiceImpl(StageRepository stageRepository,
                            ProjectRepository projectRepository,
                            TeamMemberRepository teamMemberRepository,
                            StageMapper stageMapper,
                            List<StageFilter> stageFilters,
                            List<RemoveStrategyExecutor> removeStrategies) {
        this.stageRepository = stageRepository;
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.stageMapper = stageMapper;
        this.stageFilters = stageFilters;
        removeStrategyExecutors = removeStrategies.stream()
                .collect(Collectors.toMap(
                        RemoveStrategyExecutor::getStrategyType,
                        Function.identity()));
    }

    @Override
    @Transactional
    public StageDto createStage(StageCreateDto stageCreateDto) {
        validateProject(stageCreateDto.projectId());
        Project project = projectRepository.getProjectById(stageCreateDto.projectId());
        if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
            throw new DataValidationException(
                    WRONG_PROJECT_STATUS.getMessage().formatted(stageCreateDto.projectId()));
        }
        Stage newStage = stageMapper.toStage(stageCreateDto);
        newStage.getStageRoles().forEach(role -> role.setStage(newStage));
        Stage savedStage = stageRepository.save(newStage);
        log.info("Stage created: {}", savedStage.getStageId());
        return stageMapper.toStageDto(savedStage);
    }

    @Override
    public List<StageDto> getStages(Long projectId, StageFilterDto filters) {
        validateProject(projectId);
        List<Stage> stages = projectRepository.getProjectById(projectId).getStages();
        List<Stage> filteredStages = filterStages(stages.stream(), filters);
        log.info("Found {} filtered stages for project: {}", filteredStages.size(), projectId);
        return stageMapper.toStageDtos(filteredStages);
    }

    @Override
    public StageDto removeStage(Long stageId, RemoveTypeDto removeTypeDto) {
        Stage stage = stageRepository.getById(stageId);
        removeStrategyExecutors.get(removeTypeDto.removeStrategy()).execute(stage, removeTypeDto);
        log.info("Stage removed: {}", stageId);
        return stageMapper.toStageDto(stage);
    }

    @Override
    @Transactional
    public StageDto updateStage(StageUpdateDto stageUpdateDto, Long userId, Long stageId) {
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
        checkMemberCountForRole(stage, executors, userId);
        return stageMapper.toStageDto(stageRepository.save(stage));
    }

    @Override
    public StageDto getStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        log.info("Stage found: {}", stageId);
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

    private void setStageForExecutors(Stage stage, List<TeamMember> executors) {
        for (TeamMember executor : executors) {
            executor.getStages().add(stage);
        }
    }

    private void checkMemberCountForRole(Stage stage, List<TeamMember> executors, Long userId) {
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
        List<TeamMember> projectTeamMembers = getAllMembersOfProjectWithoutExecutors(stage.getProject(), executors);
        for (Map.Entry<TeamRole, Long> entry : neededRolesCount.entrySet()) {
            List<TeamMember> foundedTeamMembers = projectTeamMembers.stream()
                    .filter(teamMember -> teamMember.getRoles().contains(entry.getKey()))
                    .limit(entry.getValue())
                    .toList();
            if (foundedTeamMembers.size() < entry.getValue()) {
                throw new EntityNotFoundException(NOT_ENOUGH_TEAM_MEMBERS_IN_PROJECT.getMessage()
                        .formatted(entry.getKey(), stage.getProject().getId()));
            }
            foundedTeamMembers.forEach(teamMember -> sendInvitation(stage, teamMember, userId));
        }
    }

    private List<TeamMember> getAllMembersOfProjectWithoutExecutors(Project project, List<TeamMember> executors) {
        return project.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> !executors.contains(teamMember))
                .toList();
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
