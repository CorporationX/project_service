package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.ProjectStatusException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageMapper stageMapper;
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public StageDto create(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());

        if (project.getStatus() == ProjectStatus.CANCELLED) {
            throw new ProjectStatusException("You can't create stage for cancelled project");
        }

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new ProjectStatusException("You can't create stage for completed project");
        }

        Stage stage = stageMapper.toStageEntity(stageDto);
        return stageMapper.toStageDto(stageRepository.save(stage));
    }

    public List<StageDto> getByStatus(ProjectStatus status) {
        return stageRepository.findByStatus(status).stream()
                .map(stageMapper::toStageDto)
                .toList();
    }

    public StageDto deleteStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasksToDelete = stage.getTasks();

        taskRepository.deleteAll(tasksToDelete);
        stageRepository.delete(stage);

        return stageMapper.toStageDto(stage);
    }

    public StageDto deleteStageWithClosingTasks(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasksToClose = stage.getTasks();
        tasksToClose.forEach(task -> task.setStatus(TaskStatus.DONE));
        taskRepository.saveAll(tasksToClose);
        stageRepository.delete(stage);

        return stageMapper.toStageDto(stage);
    }

    public StageDto update(Long stageId, StageDto stageDto) {
        Stage currentStage = stageRepository.getById(stageId);
        Project project = projectRepository.getProjectById(currentStage.getProject().getId());

        List<TeamMember> currentExecutors = currentStage.getExecutors();

        for (StageRolesDto stageRoles : stageDto.getStageRoles()) {
            List<TeamMember> teamMembers = currentExecutors.stream()
                    .filter(teamMember -> teamMember.equals(stageRoles.getTeamRole()))
                    .toList();

            if (teamMembers.isEmpty()) {
                List<TeamMember> availableMembersWithRole = findAvailableMembersWithRole
                        (project, stageRoles.getTeamRole(), stageRoles.getCount(), currentStage);
                availableMembersWithRole.forEach(teamMember -> sendInvitation(StageInvitation.builder().build()));
            }
        }

        return stageMapper.toStageDto(stageRepository.save(stageMapper.toStageEntity(stageDto)));
    }


    private List<TeamMember> findAvailableMembersWithRole(Project project, TeamRole role, long needed, Stage stage) {
        List<TeamMember> allMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();

        return allMembers.stream()
                .filter(member -> !stage.getExecutors().contains(member))
                .filter(member -> member.getRoles().contains(role))
                .limit(needed)
                .collect(Collectors.toList());
    }

    private void sendInvitation(StageInvitation stageInvitation) {
//        TODO
//         stageInvitationService.send(stageInvitation);
    }

    public List<StageDto> getAll() {
        return stageRepository.findAll().stream()
                .map(stageMapper::toStageDto)
                .toList();
    }

    public StageDto getById(long stageId) {
        return stageMapper.toStageDto(stageRepository.getById(stageId));
    }
}
