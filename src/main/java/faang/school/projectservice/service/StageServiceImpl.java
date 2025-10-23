package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.AllStageFilterDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class StageServiceImpl implements StageService {
    private final StageMapper stageMapper;
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRolesRepository stageRolesRepository;
    private final StageFilter stageFilter;
    private final StageInvitationServiceImpl stageInvitationService;
    private final UserContext userContext;

    @Transactional
    @Override
    public void createStage(StageCreateDto stageCreateDto) {
        validateTeamMember(userContext.getUserId());
        Project project = getProjectOrThrow(stageCreateDto.projectId());
        isApplicableProject(project);
        List<TeamMember> teamMembers = teamMemberRepository.findAllByIdIn(stageCreateDto.executorsId());
        List<Task> tasks = new ArrayList<>();
        Stage savedStage = stageRepository.save(stageMapper.toEntityCreate(stageCreateDto, teamMembers, project, tasks));
        Map<TeamRole, Long> stageRoles = stageCreateDto.teamRoles().stream()
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));
        List<StageRoles> stageRolesList = stageRoles.entrySet().stream()
                .map(role -> StageRoles.builder()
                        .count(role.getValue().intValue())
                        .teamRole(role.getKey())
                        .stage(savedStage)
                        .build())
                .toList();
        stageRolesRepository.saveAll(stageRolesList);
        log.info("Created new Stage in Project {} - id ", project.getId());
    }

    @Override
    public List<StageDto> getAllStageByFilter(AllStageFilterDto allStageFilterDto) {
        List<Stage> stageList = stageFilter.applyByRoleAndStatus(allStageFilterDto);
        return stageMapper.toListDto(stageList);
    }

    @Override
    public void deleteStage(Long projectId, Long stageId) {
        Project project = getProjectOrThrow(projectId);
        isApplicableProject(project);
        validateTeamMember(userContext.getUserId());
        Stage stage = getStageOrThrow(stageId);
        stageRepository.delete(stage);
    }

    @Transactional
    @Override
    public StageDto updateStage(StageUpdateDto stageUpdateDto, Long stageId) {
        validateTeamMember(userContext.getUserId());
        long countParticipant = stageUpdateDto.countParticipant();
        Stage stage = getStageOrThrow(stageId);
        executorSuperfluous(stage);
        Project project = getProjectOrThrow(stage.getProject().getId());
        isApplicableProject(project);
        List<TeamMember> memberOriginStage = stage.getExecutors().stream()
                .filter(teamMember -> teamMember.getRoles().stream()
                        .anyMatch(teamRole -> teamRole.equals(stageUpdateDto.teamRole())))
                .limit(countParticipant)
                .toList();
        if (memberOriginStage.size() != countParticipant) {
            long lastParticipants = countParticipant - memberOriginStage.size();
            return addAnotherParticipantStages(project, stage, memberOriginStage, lastParticipants, stageUpdateDto.teamRole());
        }
        return sendingInvitation(memberOriginStage, stage);
    }

    @Override
    public List<StageDto> getStages(long projectId) {
        Project project = getProjectOrThrow(projectId);
        return stageMapper.toListDto(project.getStages());
    }

    @Override
    public StageDto getStage(long stageId) {
        return stageMapper.toDto(getStageOrThrow(stageId));
    }

    private StageDto addAnotherParticipantStages(Project project, Stage verifyStage,
                                                 List<TeamMember> teamMembersSend,
                                                 long lastParticipant, TeamRole filterRole) {
        log.debug("There were not enough team members at the stage, {} - the number of those missing for the stage, {} - stage identifier, {} - stage name",
                lastParticipant, verifyStage.getStageId(), verifyStage.getStageName());
        List<Stage> stages = project.getStages().stream()
                .filter(filterStage -> !filterStage.getStageId().equals(verifyStage.getStageId()))
                .toList();
        List<TeamMember> anotherParticipants = stages.stream()
                .map(Stage::getExecutors)
                .flatMap(List::stream)
                .filter(teamMember -> teamMember.getRoles().stream()
                        .anyMatch(teamRole -> teamRole.equals(filterRole)))
                .filter(executor -> !teamMembersSend.contains(executor))
                .limit(lastParticipant)
                .toList();
        return sendingInvitation(anotherParticipants, verifyStage);
    }

    private StageDto sendingInvitation(List<TeamMember> teamMemberList, Stage stage) {
        String description = String.format("You have been invited by %s to join the %s stage!",
                userContext.getUserId(), stage.getStageName());
        List<Long> teamMembersId = new ArrayList<>();
        List<StageInvitationCreateDto> invitations = teamMemberList.stream()
                .peek(teamMember -> teamMembersId.add(teamMember.getUserId()))
                .map(teamMember ->
                        new StageInvitationCreateDto(
                                stage.getStageId(), userContext.getUserId(), teamMember.getUserId(), description))
                .toList();
        invitations.forEach(stageInvitationService::sendInvitation);
        return StageDto.builder()
                .projectId(stage.getProject().getId())
                .stageId(stage.getStageId())
                .stageName(stage.getStageName())
                .teamMemberId(teamMembersId)
                .build();
    }

    private Stage getStageOrThrow(long stageId) {
        return stageRepository.findById(stageId).orElseThrow(
                () -> {
                    log.error("Trying to find a stage that doesn't exist, param {}", stageId);
                    return new EntityNotFoundException("There is no stage for this id!");
                });
    }

    private Project getProjectOrThrow(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> {
                    log.error("Trying to find a project that doesn't exist, param {}", projectId);
                    return new EntityNotFoundException("No such project exists!");
                });
    }

    private void validateTeamMember(long teamMemberId) {
        if (!teamMemberRepository.existsById(teamMemberId)) {
            log.error("{} - User is not a team member", teamMemberId);
            throw new EntityNotFoundException("You are not a member of the team");
        }

    }

    private void executorSuperfluous(Stage stage) {
        Set<TeamRole> requiredRoles = stage.getStageRoles().stream()
                .map(StageRoles::getTeamRole)
                .collect(Collectors.toSet());

        boolean isExtra = stage.getExecutors().stream()
                .anyMatch(executor ->
                        executor.getRoles().stream()
                                .anyMatch(role -> !requiredRoles.contains(role)));
        if (isExtra) {
            throw new DataValidationException("There are extra users in the stage!");
        }
    }

    private void isApplicableProject(Project project) {
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            log.error("Trying to add a milestone to a canceled or completed project {} - id", project.getId());
            throw new ForbiddenException("You cannot create a milestone for a canceled or completed project.!");
        }
    }
}