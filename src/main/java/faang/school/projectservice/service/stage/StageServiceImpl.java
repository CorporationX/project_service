package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.mapper.StageDtoMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final StageDtoMapper stageDtoMapper;
    private final StageInvitationRepository stageInvitationRepository;

    @Override
    public StageDto findById(Long id) {
        Stage stage = stageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Stage not found"));
        return stageDtoMapper.ToStageDto(stage);
    }

    public List<StageDto> findAllStages(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Project not found")
        );
        validateProjectOnHoldOrCancelled(project.getStatus());
        return stageDtoMapper.ToStageDtoList(project.getStages());
    }

    @Transactional
    public void updateStage(StageDto stageDto) {
        ProjectStatus projectStatus = stageDto.getProject().getStatus();
        validateProjectOnHoldOrCancelled(projectStatus);
        Stage stage = stageRepository.findById(stageDto.getStageId())
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));
        stage.setStageName(stageDto.getStageName());

        Map<TeamRole, Integer> neededCounts = getTeamRoleIntegerMap(stageDto, stage);

        List<StageRoles> missingRoles = stageDto.getStageRoles().stream()
                .filter(role -> !stage.getStageRoles().contains(role))
                .toList();

        sendInvites(neededCounts, stage, missingRoles);
        stage.setStageRoles(stageDto.getStageRoles());
        stageRepository.save(stage);
    }

    @Transactional
    public void deleteStage(Long id, StageDto stageDto) {
        Stage stage = stageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Stage not found"));
        if (stageDto == null) {
            stage.getTasks().clear();
        } else {
            validateProjectOnHoldOrCancelled(stageDto.getProject().getStatus());
            stageDto.getTasks().addAll(stage.getTasks());
            stageRepository.save(stageDtoMapper.ToStage(stageDto));
            stageRepository.delete(stage);
        }
    }

    public List <StageDto> getStagesWithFilters(TeamRole teamRole, TaskStatus taskStatus) {
        List<Stage> resultList = stageRepository.findAll().stream()
                .filter(stage -> stage.getTasks().stream()
                        .anyMatch(task -> task.getStatus() == taskStatus))
                .filter(stage -> stage.getStageRoles().stream()
                        .anyMatch(stageRole -> stageRole.getTeamRole() == teamRole))
                .toList();
        System.out.println("size " + resultList.size());
        return stageDtoMapper.ToStageDtoList(resultList);
    }

    @Transactional
    public void save(StageDto stageDto) {
        ProjectStatus projectStatus = stageDto.getProject().getStatus();
        validateProjectOnHoldOrCancelled(projectStatus);
        stageRepository.save(stageDtoMapper.ToStage(stageDto));

    }

    private void sendInvites(List<TeamMember> candidates, Stage stage) {
        for (TeamMember member : candidates) {
            if (!stageInvitationRepository.existsByInvitedAndStage(member, stage)) {
                StageInvitation stageInvitation = new StageInvitation();
                stageInvitation.setStage(stage);
                stageInvitation.setInvited(member);
                stageInvitation.setStatus(StageInvitationStatus.PENDING);
                stageInvitationRepository.save(stageInvitation);
            }
        }
    }

    private void validateProjectOnHoldOrCancelled(ProjectStatus projectStatus) {
        if (projectStatus == ProjectStatus.CANCELLED
            || projectStatus == ProjectStatus.ON_HOLD
            || projectStatus == ProjectStatus.COMPLETED) {
            throw new IllegalArgumentException("Project status is " + projectStatus);
        }
    }

    private void sendInvites(Map<TeamRole, Integer> neededCounts, Stage stage, List<StageRoles> missingRoles) {
        for (Map.Entry<TeamRole, Integer> entry : neededCounts.entrySet()) {
            TeamRole role = entry.getKey();
            int countNeeded = entry.getValue();
            List<TeamMember> candidates = stage.getProject().getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(member -> member.getRoles().contains(role))
                    .filter(member -> !stageInvitationRepository.existsByInvitedAndStage(member, stage))
                    .limit(countNeeded)
                    .toList();
            sendInvites(candidates, stage);
        }

        for (StageRoles missingRole : missingRoles) {
            if (!neededCounts.containsKey(missingRole.getTeamRole())) {
                List<TeamMember> candidates = stage.getProject().getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .filter(member -> member.getRoles().contains(missingRole.getTeamRole()))
                        .filter(member -> !stageInvitationRepository.existsByInvitedAndStage(member, stage))
                        .limit(missingRole.getCount())
                        .toList();

                sendInvites(candidates, stage);
            }
        }
    }

    private  Map<TeamRole, Integer> getTeamRoleIntegerMap(StageDto stageDto, Stage stage) {
        Map<TeamRole, Integer> stageRoleCounts = stage.getStageRoles().stream().
                collect(Collectors.toMap(StageRoles::getTeamRole, StageRoles::getCount));
        Map<TeamRole, Integer> stageDtoCount = stageDto.getStageRoles().stream().
                collect(Collectors.toMap(StageRoles::getTeamRole, StageRoles::getCount));
        Map<TeamRole, Integer> neededCounts = stageDtoCount.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() - stageRoleCounts.getOrDefault(entry.getKey(), 0)
                ));
        return neededCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
