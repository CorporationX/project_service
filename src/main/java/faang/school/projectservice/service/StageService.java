package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageDtoForUpdate;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public StageDto updateStage(StageDtoForUpdate stageDto) {

        Stage stageFromRepository = stageRepository.getById(stageDto.getStageId());
        log.debug("Stage from repository is successfully retrieved");

        stageValidator.isCompletedOrCancelled(stageFromRepository);
        log.debug("Stage is not completed or cancelled");

        TeamMember author = teamMemberRepository.findById(stageDto.getAuthorId());
        List<TeamRole> teamRolesFromStageDto = stageDto.getTeamRoles();

        List<TeamRole> newTeamRoles = findNewTeamRoles(stageFromRepository, teamRolesFromStageDto);
        log.debug("New team roles are successfully retrieved");

        Stage stageAfterUpdate = setNewFieldsForStage(stageFromRepository, stageDto);
        log.debug("Stage after update is successfully retrieved");

        sendStageInvitation(stageAfterUpdate, newTeamRoles, author);
        log.debug("Stage invitation is successfully sent");

        return stageMapper.toDto(stageRepository.save(stageAfterUpdate));
    }

    private Stage setNewFieldsForStage(Stage stageFromRepository, StageDtoForUpdate stageDto) {

        Map<String, Long> teamRolesMap = stageDto.getTeamRoles()
                .stream()
                .collect(Collectors.groupingBy(TeamRole::toString, Collectors.counting()));

        List<StageRoles> newStageRoles = teamRolesMap.entrySet()
                .stream()
                .map(entry ->
                        StageRoles.builder()
                                .teamRole(TeamRole.valueOf(entry.getKey()))
                                .count(Math.toIntExact(entry.getValue()))
                                .build())
                .collect(Collectors.toList());

        newStageRoles.forEach(stageRoles ->
                stageFromRepository.getStageRoles().stream()
                        .filter(stageRolesFromRepo -> stageRolesFromRepo.getTeamRole().equals(stageRoles.getTeamRole()))
                        .forEach(stageRolesRepository::delete)
        );

        stageFromRepository.setStageRoles(newStageRoles);
        stageFromRepository.getStageRoles().forEach(stageRole -> stageRole.setStage(stageFromRepository));
        stageFromRepository.setStageName(stageDto.getStageName());
        stageFromRepository.setStatus(stageDto.getStatus());

        return stageFromRepository;
    }

    private void sendStageInvitation(Stage stage, List<TeamRole> newTemRoles, TeamMember author) {
        if (!newTemRoles.isEmpty()) {
            Project projectFromRepository = projectRepository.getProjectById(stage.getProject().getId());

            newTemRoles
                    .forEach(teamRole ->
                            projectFromRepository.getTeams()
                                    .stream()
                                    .flatMap(team -> team.getTeamMembers()
                                            .stream())
                                    .filter(teamMember -> teamMember.getRoles().contains(teamRole))
                                    .forEach(teamMember -> stageInvitationService.sendInvitation(StageInvitationDto.builder()
                                            .invited(teamMember)
                                            .author(author)
                                            .stage(stage)
                                            .build())));
        }
    }

    private List<TeamRole> findNewTeamRoles(Stage stageFromRepository, List<TeamRole> teamRoles) {
        List<TeamRole> rolesToRemove = stageFromRepository.getExecutors()
                .stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .toList();
        teamRoles.removeIf(rolesToRemove::contains);

        return teamRoles;
    }
}