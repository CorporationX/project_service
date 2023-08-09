package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageValidator stageValidator;
    private final StageInvitationService stageInvitationService;
    private final StageRolesRepository stageRolesRepository;
    private final StageRolesMapper stageRolesMapper;
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
    public StageDto updateStage(StageDto stageDto, Long stageId, Long authorId) {

        Stage stage = stageRepository.getById(stageId);
        log.debug("Stage from repository is successfully retrieved");

        stageValidator.isCompletedOrCancelled(stage);
        log.debug("Stage is not completed or cancelled");

        TeamMember author = teamMemberRepository.findById(authorId);
        log.debug("Author from repository is successfully retrieved");

        List<TeamRole> newTeamRoles = findNewTeamRoles(stage, stageDto);
        log.debug("New team roles are successfully retrieved");

        Stage stageAfterUpdate = setNewFieldsForStage(stage, stageDto);
        log.debug("Stage after update is successfully retrieved");

        sendStageInvitation(stageAfterUpdate, newTeamRoles, author);
        log.debug("Stage invitation is successfully sent");

        return stageMapper.toDto(stageRepository.save(stageAfterUpdate));
    }

    public Stage setNewFieldsForStage(Stage stage, StageDto stageDto) {
        List<TeamRole> teamRoles = getStageRoles(stageDto);

        Map<String, Long> stageDtoTeamRolesMap = getMapTeamRoles(teamRoles);

        List<StageRoles> newStageRoles = getStageRoles(stageDtoTeamRolesMap);

        deleteStageRoles(stage, newStageRoles);

        stage.setStageRoles(newStageRoles);
        stage.getStageRoles().forEach(stageRole -> stageRole.setStage(stage));
        stage.setStageName(stageDto.getStageName());
        stage.setStatus(StageStatus.valueOf(stageDto.getStatus()));

        return stage;
    }

    private void deleteStageRoles(Stage stage, List<StageRoles> newStageRoles) {
        newStageRoles.forEach(stageRoles ->
                stage.getStageRoles().stream()
                        .filter(stageRolesFromRepo -> stageRolesFromRepo.getTeamRole().equals(stageRoles.getTeamRole()))
                        .forEach(stageRolesRepository::delete)
        );
    }

    private static List<StageRoles> getStageRoles(Map<String, Long> stageDtoTeamRolesMap) {
        return stageDtoTeamRolesMap.entrySet()
                .stream()
                .map(entry ->
                        StageRoles.builder()
                                .teamRole(TeamRole.valueOf(entry.getKey()))
                                .count(Math.toIntExact(entry.getValue()))
                                .build())
                .collect(Collectors.toList());
    }

    private Map<String, Long> getMapTeamRoles(List<TeamRole> teamRoles) {
        return teamRoles
                .stream()
                .collect(Collectors.groupingBy(TeamRole::toString, Collectors.counting()));
    }


    public List<TeamRole> findNewTeamRoles(Stage stage, StageDto stageDto) {
        List<TeamRole> teamRoles = getStageRoles(stageDto);

        List<TeamRole> rolesToRemove = stage.getExecutors()
                .stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .toList();
        teamRoles.removeIf(rolesToRemove::contains);

        return teamRoles;
    }

    private List<TeamRole> getStageRoles(StageDto stageDto) {
        List<StageRoles> stageDtoStageRoles = stageDto.getStageRolesDto().stream().map(stageRolesMapper::toEntity).toList();

        return stageDtoStageRoles.stream()
                .flatMap(stageRoles -> Collections.nCopies(stageRoles.getCount(), stageRoles.getTeamRole()).stream())
                .collect(Collectors.toList());
    }

    public void sendStageInvitation(Stage stage, List<TeamRole> newTeamRoles, TeamMember author) {
        if (!newTeamRoles.isEmpty()) {
            List<TeamMember> projectTeamMembers = getTeamMembers(stage);
            sendingInvitation(stage, newTeamRoles, author, projectTeamMembers);
        }
    }

    private void sendingInvitation(Stage stage, List<TeamRole> newTeamRoles, TeamMember author, List<TeamMember> projectTeamMembers) {
        newTeamRoles.forEach(teamRole ->
                projectTeamMembers.stream()
                        .filter(teamMember -> teamMember.getRoles().contains(teamRole))
                        .findFirst()
                        .ifPresent(teamMember -> stageInvitationService.sendInvitation(StageInvitationDto.builder()
                                .invited(teamMember)
                                .author(author)
                                .stage(stage)
                                .build()))
        );
    }

    private List<TeamMember> getTeamMembers(Stage stage) {
        Project project = projectRepository.getProjectById(stage.getProject().getId());

        return project.getTeams()
                .stream()
                .map(Team::getTeamMembers)
                .flatMap(Collection::stream)
                .toList();
    }
}