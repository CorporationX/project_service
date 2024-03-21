package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.exception.ProjectStatusException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    public void makeStage(String stageName, Long projectId, List<StageRoles> stageRoles) {
        List<Task> tasks = new ArrayList<>();
        List<TeamMember> executors = new ArrayList<>();
        makeStage(stageName, projectId, stageRoles, tasks, executors);
    }

    public void makeStage(String stageName, Long projectId, List<StageRoles> stageRoles, List<Task> tasks, List<TeamMember> executors) {
        if (projectRepository.getProjectById(projectId).getStatus() == ProjectStatus.CANCELLED) {
            throw new ProjectStatusException("You can not make stage for cancelled project!");
        }
        if (projectRepository.getProjectById(projectId).getStatus() == ProjectStatus.COMPLETED) {
            throw new ProjectStatusException("You can not make stage for completed project!");
        }
        Stage stage = new Stage();
        stage.setStageName(stageName);
        stage.setProject(projectRepository.getProjectById(projectId));
        stage.setStageRoles(stageRoles);
        stage.setTasks(tasks);
        stage.setExecutors(executors);
        stageRepository.save(stage);
    }

    public List<StageDto> getStagesByStatus(Long projectId, TaskStatus status) {
        List<Stage> projectStages = stageRepository.findAll().stream()
                .filter(stage -> stage.getProject().getId() == projectId).toList();
        return stageMapper.toDto(projectStages.stream()
                .filter(stage -> {
                    if (stage.getTasks().stream().filter(task -> task.getStatus().equals(status)).toList().isEmpty()) {
                        return false;
                    }
                    return true;
                })
                        .toList());
    }

    public void deleteStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();
        for (Task task : tasks) {
            task.setStatus(TaskStatus.CANCELLED);
        }
        stageRepository.delete(stage);
    }

    public void deleteStage(Long stageIdToDelete, Long stageIdToReceive) {
        Stage stageToDelete = stageRepository.getById(stageIdToDelete);
        Stage stageToReceive = stageRepository.getById(stageIdToReceive);
        List<Task> tasks = stageToDelete.getTasks();
        for (Task task : tasks) {
            task.setStage(stageToReceive);
        }
        stageRepository.delete(stageToDelete);
    }

    public void updateStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        Project project = stage.getProject();
        List<TeamMember> executors = stage.getExecutors();
        for (StageRoles stageRole : stage.getStageRoles()) {
            TeamRole teamRole = stageRole.getTeamRole();
            List<TeamMember> membersWithRole = executors.stream()
                    .filter(teamMember -> teamMember.getRoles().contains(teamRole)).toList();
            if (membersWithRole.size() < stageRole.getCount()) {
                List<Team> teams = project.getTeams();
                List<TeamMember> teamMembersWithInvitation = new ArrayList<>();
                int countOfInvitations = stageRole.getCount() - membersWithRole.size();
                int i = 0;
                for (Team team : teams) {
                    List<TeamMember> teamMemberToInvitate = team.getTeamMembers().stream()
                            .filter(teamMember -> teamMember.getRoles().contains(stageRole.getTeamRole()))
                            .filter(teamMember -> !membersWithRole.contains(teamMember))
                            .filter(teamMember -> !teamMembersWithInvitation.contains(teamMember))
                            .toList();
                    for (TeamMember teamMember : teamMemberToInvitate) {
                            // отправить приглашение участнику - нужен метод
                            teamMembersWithInvitation.add(teamMember);
                            i++;
                            if (i >= countOfInvitations) {
                                break;
                        }
                    }
                }
            }
        }
    }

    public List<StageDto> getAllStages(Long projectId) {
        return stageMapper.toDto(stageRepository.findAll().stream()
                .filter(stage -> stage.getProject().equals(projectRepository.getProjectById(projectId))).toList());
    }

    public StageDto getStage(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}
