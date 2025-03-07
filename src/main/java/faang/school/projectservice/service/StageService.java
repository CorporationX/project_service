package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageCreateRequestDto;
import faang.school.projectservice.dto.stage.StageCreateResponseDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageResponseDto;
import faang.school.projectservice.dto.stage.StageUpdateRequestDto;
import faang.school.projectservice.dto.stage.StageUpdateResponseDto;
import faang.school.projectservice.exception.StageUpdateException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.stratagy.stage.StageDeletionStrategy;
import faang.school.projectservice.stratagy.stage.StageDeletionStrategyFactory;
import faang.school.projectservice.stratagy.stage.StageDeletionType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;
    private final StageDeletionStrategyFactory stageDeletionStrategyFactory;

    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private StageInvitationService stageInvitationService;

    public StageCreateResponseDto createStage(StageCreateRequestDto stageCreateRequestDto) {
        Stage stage = stageMapper.toStage(stageCreateRequestDto);
        Project project = projectService.getProjectById(stageCreateRequestDto.getProjectId());
        stage.setProject(project);
        List<StageRoles> stageRoles = stage.getStageRoles();
        for (StageRoles stageRole : stageRoles) {
            stageRole.setStage(stage);
        }
        Stage savedStage = stageRepository.save(stage);
        return stageMapper.toCreateResponseDto(savedStage);
    }

    public List<StageResponseDto> getAllProjectStages(Long projectId, StageFilterDto stageFilterDto) {
        Stream<Stage> stageStream = stageRepository.findAllByProjectId(projectId);

        if (stageFilterDto != null && CollectionUtils.isNotEmpty(stageFilters)) {
            for (StageFilter stageFilter : stageFilters) {
                if (stageFilter.isApplicable(stageFilterDto)) {
                    stageStream = stageFilter.apply(stageStream, stageFilterDto);
                }
            }
        }
        return stageStream.map(stageMapper::toResponseDto)
                .toList();
    }


    public void deleteStage(Long stageId, StageDeletionType deletionType) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("No stage found with id: " + stageId));
        StageDeletionStrategy strategy = stageDeletionStrategyFactory.getStrategy(deletionType);
        strategy.handleTasksBeforeStageDeletion(stage);
        stageRepository.delete(stage);
    }

    public StageUpdateResponseDto updateStage(StageUpdateRequestDto stageUpdateRequestDto) {
        Stage stage = getStageById(stageUpdateRequestDto.getStageId());
        stageMapper.updateStage(stageUpdateRequestDto, stage);
        stageMapper.updateStageRoles(stageUpdateRequestDto.getStageRolesDtos(), stage.getStageRoles());
        stage.setExecutors(teamMemberService.findAllByIds(stageUpdateRequestDto.getTeamMemberIds()));

        validateStageRoles(stage, stageUpdateRequestDto.getStageUpdateAuthorId());

        return stageMapper.toUpdateResponseDto(stageRepository.save(stage));
    }

    public StageResponseDto getStageDtoById(Long stageId) {
        return stageMapper.toResponseDto(getStageById(stageId));
    }

    public Stage getStageById(Long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("No stage found with id: " + stageId));
    }

    @Autowired
    public void setStageInvitationService(@Lazy StageInvitationService stageInvitationService) {
        this.stageInvitationService = stageInvitationService;
    }

    private void validateStageRoles(Stage stage, long stageUpdateAuthorId) {
        List<TeamMember> teamMembers = stage.getExecutors()
                .stream()
                .filter(Objects::nonNull)
                .toList();
        List<StageRoles> stageRoles = stage.getStageRoles()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        List<TeamRole> uncoveredRoles = getUncoveredRoles(teamMembers, stageRoles);

        for (TeamRole uncoveredRole : uncoveredRoles) {
            sendStageInvitationToProjectMembers(uncoveredRole, stage.getProject(), stage, stageUpdateAuthorId);
        }
    }

    private void sendStageInvitationToProjectMembers(TeamRole uncoveredRole,
                                                     Project project,
                                                     Stage stage,
                                                     Long stageUpdateAuthorId) {
        try {
            project.getTeams().stream()
                    .map(Team::getTeamMembers)
                    .filter(CollectionUtils::isNotEmpty)
                    .flatMap(Collection::stream)
                    .filter(teamMember -> teamMember != null &&
                            teamMember.getRoles().contains(uncoveredRole))
                    .forEach(teamMember ->
                            stageInvitationService.createStageInvitationAndGetDto(stage,
                                    stageUpdateAuthorId,
                                    teamMember));
        } catch (Exception e) {
            throw new StageUpdateException("Cannot send stage invitation to project members", e);
        }
    }

    private List<TeamRole> getUncoveredRoles(List<TeamMember> teamMembers, List<StageRoles> stageRoles) {
        Map<TeamRole, Long> coveredNumberInRolesOnStage = getCoveredNumberInRolesOnStage(teamMembers);
        List<TeamRole> uncoveredRoles = new ArrayList<>();
        for (StageRoles stageRole : stageRoles) {
            if (stageRole != null) {
                TeamRole teamRole = stageRole.getTeamRole();
                Long coveredNumberInRole = coveredNumberInRolesOnStage.getOrDefault(teamRole, 0L);
                if (coveredNumberInRole < stageRole.getCount()) {
                    uncoveredRoles.add(teamRole);
                }
            }
        }
        return uncoveredRoles;
    }

    private Map<TeamRole, Long> getCoveredNumberInRolesOnStage(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getRoles)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
