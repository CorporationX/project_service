package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.mapper.stage.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TaskService taskService;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;
    private final UserContext userContext;
    private final StageInvitationRepository stageInvitationRepository;


    public StageDto createStage(StageDto stageDto) {
        validationStageDto(stageDto);
        Stage saved = stageRepository.save(stageMapper.toEntity(stageDto));
        return stageMapper.toDto(saved);
    }

    public void deleteStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        taskService.cancelTasksOfStage(stageId);
        stageRepository.delete(stage);
    }

    public StageDto getStageById(Long stageId) {
        Stage stageById = stageRepository.getById(stageId);
        return stageMapper.toDto(stageById);
    }

    public List<StageDto> findAllStagesOfProject(Long projectId) {
        return projectRepository.getProjectById(projectId)
                .getStages()
                .stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto updateStage(Long stageId, StageRolesDto stageRolesDto) {
        Stage stageToUpdate = stageRepository.getById(stageId);
        Project project = projectRepository.getProjectById(stageToUpdate.getProject().getId());

        Integer countTeamMembers = project.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getStages().contains(stageToUpdate))
                .filter(teamMember -> teamMember.getRoles().contains(stageRolesDto.getTeamRole()))
                .toList().size();

        int executorsCount = stageRolesDto.getCount() - countTeamMembers;

        if (stageRolesDto.getCount() > countTeamMembers) {
            List<TeamMember> teamMembers = project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(teamMember -> !stageToUpdate.getStageRoles().contains(teamMember))
                    .filter(teamMember -> teamMember.getRoles().contains(stageRolesDto.getTeamRole()))
                    .distinct()
                    .toList();

            for (TeamMember teamMember : teamMembers) {
                if (executorsCount == 0) {
                    break;
                }
                sendStageInvitation(stageId, teamMember.getId());
                executorsCount--;
            }
        }

        List<StageRoles> stageRoles = updateStageRoles(stageRolesDto, stageToUpdate);
        stageToUpdate.setStageRoles(stageRoles);

        stageRepository.save(stageToUpdate);
        return stageMapper.toDto(stageToUpdate);

    }

    public List<StageDto> getStagesOfProjectWithFilter(Long projectId, StageStatus status) {
        List<Stage> stages = projectRepository.getProjectById(projectId).getStages();
        return stages.stream()
                .filter(stage -> stage.getStatus().equals(status))
                .map(stageMapper::toDto)
                .toList();
    }


    private List<StageRoles> updateStageRoles(StageRolesDto stageRolesDto, Stage stage) {
        List<StageRoles> stageRoles = stage.getStageRoles();
        stageRoles.removeIf(stageRole -> stageRole.getId().equals(stageRolesDto.getId()));
        stageRoles.add(stageRolesMapper.toEntity(stageRolesDto));

        return stageRoles;
    }

    private void sendStageInvitation(long stageId, long invitedTeamMember) {
        TeamMember author = TeamMember.builder().id(userContext.getUserId()).build();
        StageInvitation stageInvitation = StageInvitation.builder()
                .author(author)
                .invited(TeamMember.builder().id(invitedTeamMember).build())
                .description("You are invited on stage " + stageId)
                .status(StageInvitationStatus.PENDING)
                .build();

        stageInvitationRepository.save(stageInvitation);
    }

    private void validationStageDto(StageDto stageDto) {
        Project projectById = projectRepository.getProjectById(stageDto.getProjectId());

        if (projectById.getStatus().equals(ProjectStatus.COMPLETED) ||
                projectById.getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("You cannot create a stage in a closed or canceled project");
        }
    }
}
