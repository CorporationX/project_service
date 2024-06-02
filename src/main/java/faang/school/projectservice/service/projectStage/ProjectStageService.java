package faang.school.projectservice.service.projectStage;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validation.StageValidator;
import faang.school.projectservice.validation.ProjectStageMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.PENDING;

@Service
@RequiredArgsConstructor
public class ProjectStageService {
    private final StageValidator stageValidator;
    private final StageMapper stageMapper;
    private final StageRepository stageRepository;
    private final StageInvitationRepository stageInvitationRepository;
    private final ProjectRepository projectRepository;

    public StageDto createStage(StageDto stageDto) {
        stageValidator.projectValidateCreate(stageDto.getProjectId());

        Stage stage = stageMapper.toEntity(stageDto);
        stage.getStageRoles().forEach((role) -> role.setStage(stage));

        return stageMapper.toDto(stageRepository.save(stage));
    }

    public List<StageDto> getStagesProjectByStatus(Long projectId, TaskStatus status) {
        return stageRepository.findAll().stream()
                .filter(stage -> stage.getProject().getId() == projectId)
                .filter(stage -> !stage.getTasks().stream()
                        .filter(task -> task.getStatus().equals(status)).toList().isEmpty())
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        tasks.forEach(task -> {
            if (task.getStatus() != TaskStatus.DONE) {
                task.setStatus(TaskStatus.CANCELLED);
            }
        });

        stageRepository.delete(stage);
    }

    public StageDto updateStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<StageRoles> stageRoles = stage.getStageRoles();
        List<TeamMember> teamMembers = stage.getExecutors();

        Map<TeamRole, Long> missedMembers = getMissedMembers(stageRoles, teamMembers);
        if (!missedMembers.isEmpty()) {
            inviteMember(stage, missedMembers);
        }

        return stageMapper.toDto(stageRepository.getById(stage.getStageId()));
    }

    public List<StageDto> getAllStages(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException(ProjectStageMessage.PROJECT_STAGE_NOT_FOUND.getMessage());
        }

        return stageRepository.findAll().stream()
                .filter(stage -> stage.getProject().equals(projectRepository.getProjectById(projectId)))
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStage(Long stageId) {
        if (!stageRepository.existsById(stageId)) {
            throw new DataValidationException(ProjectStageMessage.STAGE_NOT_FOUND.getMessage());
        }

        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    private Map<TeamRole, Long> getMissedMembers(List<StageRoles> stageRoles, List<TeamMember> teamMembers) {
        Map<TeamRole, Long> missedMembers = new HashMap<>();
        stageRoles.forEach((stageRole) -> {
                    TeamRole teamRole = stageRole.getTeamRole();
                    long countMemberForRole = teamMembers.stream()
                            .filter((member) -> member.getRoles().contains(teamRole))
                            .count();
                    long deltaMembers = stageRole.getCount() - countMemberForRole;
                    if (deltaMembers > 0) {
                        missedMembers.put(teamRole, deltaMembers);
                    }
                }
        );

        return missedMembers;
    }

    private void inviteMember(Stage stage, Map<TeamRole, Long> missedMembers) {
        Project project = projectRepository.getProjectById(stage.getProject().getId());
        List<Team> teamsProject = project.getTeams();
        List<TeamMember> members = teamsProject.stream()
                .flatMap((team -> team.getTeamMembers().stream()
                        .filter((member) -> !member.getStages().contains(stage))))
                .toList();

        missedMembers.forEach((key, value) -> members.stream()
                .filter(member -> member.getRoles().contains(key))
                .limit(value)
                .forEach(member -> sendInvite(member, stage)));
    }

    private void sendInvite(TeamMember member, Stage stage) {
        StageInvitation stageInvitation = StageInvitation.builder()
                .description("Вы были приглашены для участия в проекте " + stage.getProject().getName())
                .invited(member)
                .stage(stage)
                .status(PENDING)
                .build();

        stageInvitationRepository.save(stageInvitation);
    }
}
