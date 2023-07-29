package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageDtoForUpdate;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final StageValidator stageValidator;
    private final ProjectRepository projectRepository;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage.getStageRoles().forEach(stageRole ->
                stageRole.setStage(stage)
        );
        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStagesByStatus(String status) {
        List<Stage> stages = stageRepository.findAll();
        stages.removeIf(stage -> !stage.getStatus().toString().equalsIgnoreCase(status));
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional
    public void deleteStageById(Long stageId) {
        stageRepository.deleteById(stageId);
    }

    @Transactional
    public StageDto updateStage(StageDtoForUpdate stageDto) {

        Stage stageFromRepository = stageRepository.getById(stageDto.getStageId());

        stageValidator.isCompletedOrCancelled(stageFromRepository);

        findNewTeamRoles(stageFromRepository, stageDto);
        sendStageInvitation(stageFromRepository, stageDto);
        setNewFieldsForStage(stageFromRepository, stageDto);

        return stageMapper.toDto(stageRepository.save(stageFromRepository));
    }

    private void setNewFieldsForStage(Stage stageFromRepository, StageDtoForUpdate stageDto) {
        List<TeamRole> teamRoles = stageDto.getTeamRoles();
        Map<String, Long> teamRolesMap = teamRoles.stream().collect(Collectors.groupingBy(TeamRole::toString, Collectors.counting()));
        List<StageRoles> newStageRoles = new ArrayList<>();

        for (Map.Entry<String, Long> entry : teamRolesMap.entrySet()) {
            Integer count = Math.toIntExact(entry.getValue());
            newStageRoles.add(StageRoles.builder().teamRole(TeamRole.valueOf(entry.getKey())).count(count).build());
        }
        stageFromRepository.setStageRoles(newStageRoles);
        stageFromRepository.setStatus(StageStatus.valueOf(stageDto.getStatus()));
    }

    private void sendStageInvitation(Stage stageFromRepository, StageDtoForUpdate stageDto) {
        Project projectFromRepository = projectRepository.getProjectById(stageFromRepository.getProject().getId());

        stageDto.getTeamRoles().forEach(teamRole -> projectFromRepository.getTeams().forEach(team -> {
            if (team.getTeamMembers().contains(teamRole)) {
                StageInvitation.builder().stage(stageFromRepository).status(StageInvitationStatus.PENDING).build();
            }
        }));
    }

    private void findNewTeamRoles(Stage stageFromRepository, StageDtoForUpdate stageDto) {
        stageFromRepository.getExecutors()
                .forEach(teamMember -> teamMember.getRoles().forEach(teamRole -> stageDto.getTeamRoles()
                        .removeIf(role -> role.equals(teamRole))));
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}