package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDtoForRequest;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.StageException;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> filters;

    public StageDtoForRequest createStage(StageDtoForRequest stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        stage.setProject(project);
        List<TeamMember> executors = stageDto.getExecutorIds()
                .stream()
                .map(teamMemberRepository::findById)
                .toList();
        stage.setExecutors(executors);

        if (stage.getProject().getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new StageException(ErrorMessage.PROJECT_CANCELED);
        }
        if (stage.getProject().getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new StageException(ErrorMessage.PROJECT_COMPLETED);
        }
        stage.getExecutors().forEach(teamMember -> {
            if (teamMember.getRoles().isEmpty()) {
                throw new StageException("Team member with id: " + teamMember.getId() + "has no role");
            }
        });
        Stage saveStage = stageRepository.save(stage);
        return stageMapper.toDto(saveStage);
    }

    public List<StageDtoForRequest> getFilteredStages(StageFilterDto filterDto) {
        Stream<Stage> stages = stageRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(stages, filterDto))
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(StageDtoForRequest stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.delete(stage);
    }

    public StageDtoForRequest updateStage(StageDtoForRequest stageDto, TeamRole teamRole, int number) {
        Stage stage = stageMapper.toEntity(stageDto);
        //List<StageRoles> stageRolesList = stage.getStageRoles();
        stage.getStageRoles().forEach(
                stageRoles -> getExecutorsForRole(stage, stageRoles));
        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toDto(updatedStage);
    }

    private void getExecutorsForRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = new ArrayList<>();
        stage.getExecutors().forEach(executor -> {
            if (executor.getRoles()
                    .contains(stageRoles.getTeamRole())) {
                executorsWithTheRole.add(executor);
            }
        });

        List<TeamMember> projectMembersWithTheSameRole = new ArrayList<>();
        if (executorsWithTheRole.size() < stageRoles.getCount()) {
            stage.getProject()
                    .getTeams()
                    .forEach(team ->
                            projectMembersWithTheSameRole.addAll(
                                    team.getTeamMembers()
                                            .stream()
                                            .filter(teamMember ->
                                                    teamMember.getRoles()
                                                            .contains(stageRoles.getTeamRole()))
                                            .toList()));
        }

        int requiredNumberOfInvitation = stageRoles.getCount() - executorsWithTheRole.size();

        if (projectMembersWithTheSameRole.size() < requiredNumberOfInvitation) {
            projectMembersWithTheSameRole.forEach(teamMember -> {
                StageInvitation stageInvitationToSend = getStageInvitation(teamMember, stage, stageRoles);
                stageInvitationRepository.save(stageInvitationToSend);
            });

            int numberOfMissingTeamMembers = requiredNumberOfInvitation - projectMembersWithTheSameRole.size();

            throw new StageException("To work at the project stage, " + stageRoles.getCount() +
                    " participants with a role " + stageRoles + " are required. But there are only " +
                    projectMembersWithTheSameRole.size() + " participants on the project with this role. " +
                    " There is a need for two more participants." + numberOfMissingTeamMembers);
        }

        projectMembersWithTheSameRole.stream()
                .limit(requiredNumberOfInvitation)
                .forEach(teamMember -> {
                    StageInvitation stageInvitationToSend = getStageInvitation(teamMember, stage, stageRoles);
                    stageInvitationRepository.save(stageInvitationToSend);
                });
    }

    private StageInvitation getStageInvitation(TeamMember invited, Stage stage, StageRoles stageRoles) {
        StageInvitation stageInvitationToSend = new StageInvitation();
        String INVITATIONS_MESSAGE = String.format("%s, invite you to participate in the development stage %s " +
                "of the project %s for the role %s", stage.getStageName(), stage.getProject().getName(), stageRoles);
        stageInvitationToSend.setDescription(INVITATIONS_MESSAGE);
        stageInvitationToSend.setStatus(StageInvitationStatus.PENDING);
        stageInvitationToSend.setStage(stage);
        stageInvitationToSend.setInvited(invited);
        return stageInvitationToSend;
    }

    public List<StageDtoForRequest> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stageMapper.toDto(stages);
    }

    public StageDtoForRequest getStage(Long id) {
        Stage stage = stageRepository.getById(id);
        return stageMapper.toDto(stage);
    }
}
