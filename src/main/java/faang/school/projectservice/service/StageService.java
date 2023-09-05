package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage_roles.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;
    private final ProjectRepository projectRepository;
    private final StageValidator stageValidator;
    private final StageInvitationService stageInvitationService;
    private final StageRolesRepository stageRolesRepository;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        log.debug("StageDto mapped to entity is successfully");
        stage.getStageRoles().forEach(stageRole -> stageRole.setStage(stage));
        log.debug("Sat stage on field stage in StageRoles");
        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStagesByStatus(String status) {
        List<Stage> stages = stageRepository.findAll();
        log.debug("All stages are successfully retrieved");
        stages.removeIf(stage -> !stage.getStatus().toString().equalsIgnoreCase(status));
        log.debug("Filtered stages are successfully retrieved");
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional
    public void deleteStageById(Long stageId) {
        stageRepository.deleteById(stageId);
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        log.debug("All stages are successfully retrieved");
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    @Transactional
    public StageRolesDto updateStage(StageRolesDto stageRolesDto, Long stageId, Long authorId) {

        Stage stage = stageRepository.getById(stageId);
        log.debug("Stage from repository is successfully retrieved");

        stageValidator.isCompletedOrCancelled(stage);
        log.debug("Stage is not completed or cancelled");

        long countTeamRoles = getTeamRolesCount(stageRolesDto, stage);
        log.debug("Team roles count is successfully retrieved, count: " + countTeamRoles);

        if (countTeamRoles >= stageRolesDto.getCount()) {
            throw new DataValidationException(stageRolesDto.getTeamRole() + " enough, no more needed");
        }
        inviteTeamMemberToStage(stage, stageRolesDto, authorId, countTeamRoles);
        log.debug("Team member is successfully invited");

        updateStageRoles(stageRolesDto, stage);
        log.debug("Stage roles are successfully updated");

        return stageRolesDto;
    }

    public void updateStageRoles(StageRolesDto stageRolesDto, Stage stage) {
        List<StageRoles> stageRoles = stage.getStageRoles();
        for (StageRoles stageRole : stageRoles) {
            if (stageRole.getTeamRole().equals(stageRolesDto.getTeamRole())) {
                if (stageRole.getCount() < stageRolesDto.getCount()) {
                    stageRole.setCount(stageRolesDto.getCount());
                    stageRolesRepository.save(stageRole);
                }
            } else {
                StageRoles newStageRole = stageRolesMapper.toEntity(stageRolesDto);
                stageRolesRepository.save(newStageRole);
                stageRoles.add(newStageRole);
            }
        }
        stage.setStageRoles(stageRoles);
        stageRepository.save(stage);
    }

    public void inviteTeamMemberToStage(Stage stage, StageRolesDto stageRolesDto, Long authorId, long countTeamRoles) {
        Long projectId = stage.getProject().getId();
        List<TeamMember> projectTeamMembers = getTeamMembers(projectId);
        projectTeamMembers.stream().filter(teamMember -> teamMember.getStages().stream()
                        .noneMatch(stage1 -> stage1.getStageId().equals(stage.getStageId())))
                .filter(teamMember -> teamMember.getRoles().contains(stageRolesDto.getTeamRole()))
                .limit(stageRolesDto.getCount() - countTeamRoles)
                .forEach(teamMember -> sendStageInvitation(stage, authorId, teamMember));
    }

    public long getTeamRolesCount(StageRolesDto stageRolesDto, Stage stage) {
        return stage.getStageRoles()
                .stream()
                .filter(stageRole ->
                        stageRole.getTeamRole().equals(stageRolesDto.getTeamRole()))
                .count();
    }

    public List<TeamMember> getTeamMembers(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        return project.getTeams()
                .stream()
                .map(Team::getTeamMembers)
                .flatMap(Collection::stream)
                .toList();
    }


    public void sendStageInvitation(Stage stage, Long authorId, TeamMember teamMember) {
        StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                .stageId(stage.getStageId())
                .authorId(authorId)
                .invitedId(teamMember.getId())
                .build();
        stageInvitationService.sendInvitation(stageInvitationDto);
    }
}