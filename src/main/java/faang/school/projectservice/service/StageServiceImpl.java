package faang.school.projectservice.service;

import faang.school.projectservice.client.NotificationServiceClient;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final NotificationServiceClient notificationService;

    @Override
    public StageDto createStage(StageDto stageDto) {
        log.info("Create stage for project id={}", stageDto.projectId());
        validateCreateStageDto(stageDto);
        Project project = projectRepository.findById(stageDto.projectId())
                .orElseThrow(() -> {
                    log.debug("Project with id={} is not found", stageDto.projectId());
                    return new EntityNotFoundException("Project with id "
                            + stageDto.projectId() + " is not found");
                });

        Stage stage = stageMapper.toStage(stageDto);
        stage.setProject(project);

        buildStageRoles(stageDto, stage);

        Stage savedStage = stageRepository.save(stage);
        log.info("Stage '{}' was created for id={}", stage.getStageName(), stageDto.projectId());
        return stageMapper.toStageDto(savedStage);
    }

    @Override
    public List<StageDto> getAllStagesOfProject(Long projectId) {
        log.info("Getting all stages of project id={}", projectId);
        List<Stage> stages = stageRepository.findByProjectId(projectId);
        return stageMapper.toListStageDto(stages);
    }

    @Override
    public void deleteById(Long stageId) {
        log.info("Delete stage id={}", stageId);
        stageRepository.deleteById(stageId);
        log.info("Stage '{}' was deleted ", stageId);
    }

    @Override
    public StageDto updateStage(Long stageId, UpdateStageDto updateStageDto) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage with id "
                        + stageId + " is not found"));

        stageMapper.updateStage(updateStageDto, stage);
        TeamMemberDto teamMemberDto = updateStageDto.requiredRoles();
        boolean isRoleInStage = haveRoleInStage(stage, teamMemberDto.role());
        if (!isRoleInStage) {
            TeamMember memberWithRoleInProject = getMemberWithRoleInProject(updateStageDto.projectId()
                    , teamMemberDto.role());
            sendInvitation(memberWithRoleInProject, stage);
        }
        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toStageDto(updatedStage);
    }

    @Override
    public StageDto getById(Long stageId) {
        log.info("Getting Stage id={}", stageId);
        Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new EntityNotFoundException("Stage with id "
                + stageId + " is not found"));
        return stageMapper.toStageDto(stage);
    }

    private void validateCreateStageDto(StageDto stageDto) {
        log.info("Validation CreateStageDto for project id={}", stageDto.projectId());
        if (stageDto.stageName() == null || stageDto.stageName().isBlank()) {
            log.error("Validation error: stageName is empty for project id={}", stageDto.projectId());
            throw new DataValidationException("Name should not be empty!");
        }
        if (stageDto.requiredRoles() == null || stageDto.requiredRoles().isEmpty()) {
            log.error("Validation error:requiredRoles is empty for project id={}", stageDto.projectId());
            throw new DataValidationException("Name should not be empty!");
        }
        log.info("Validation CreateStageDto done for project id={}", stageDto.projectId());
    }

    private void buildStageRoles(StageDto stageDto, Stage stage) {
        log.info("Creation StageRoles for stage '{}' of project id={}", stage.getStageName(), stageDto.projectId());
        List<StageRoles> stageRoles = new ArrayList<>();
        stageDto.requiredRoles().forEach(roleDto -> {
            StageRoles sr = new StageRoles();
            sr.setTeamRole(roleDto.role());
            sr.setCount(roleDto.count());
            sr.setStage(stage);
            stageRoles.add(sr);
            log.info("Added role '{}'with amounts {} for stage '{}'", roleDto.role(), roleDto.count(),
                    stage.getStageName());
        });
        stage.setStageRoles(stageRoles);
        log.info("All StageRoles were created for the stage '{}'", stage.getStageName());
    }

    private TeamMember getMemberWithRoleInProject(Long projectId, TeamRole teamRole) {
        return projectRepository.findById(projectId)
                .flatMap(project -> project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .filter(teamMember -> teamMember.getRoles().contains(teamRole))
                        .findFirst()).orElseThrow(() -> new DataValidationException(
                        "No team member with role " + teamRole + " found in project " + projectId
                ));
    }

    private boolean haveRoleInStage(Stage stage, TeamRole teamRole) {
        return stage.getExecutors().stream().anyMatch(teamMember -> teamMember.getRoles().stream()
                .anyMatch(role -> role.equals(teamRole)));
    }


    private void sendInvitation(TeamMember member, Stage stage) {
        try {
            notificationService.sendStageInvitation(member.getId());
            log.info("Invitation sent to user {} for stage {}", member.getId(), stage.getStageId());
        } catch (Exception e) {
            log.error("Failed to send invitation to user {}: {}", member.getId(), e.getMessage());
        }
    }
}
