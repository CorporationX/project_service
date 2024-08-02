package faang.school.projectservice.validator.task;

import faang.school.projectservice.exception.task.TaskExceptionHandler;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskValidator {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void validateTeamMember(Long memberId, Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember member = teamMemberRepository.findById(memberId);
        boolean isMemberExist = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().contains(member));
        if (!isMemberExist) {
            throw new TaskExceptionHandler("The user is not a member of the project team!");
        }
    }

    public Task validateTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() ->
                new TaskExceptionHandler("The task does not exist!"));
    }
    public boolean descriptionIsNull(String description) {
        return description == null;
    }

    public boolean statusIsNull(String status) {
        return status == null;
    }

    public boolean performerUserIdIsNull(Long performerUserId) {
        return performerUserId == null;
    }

    public boolean parentTaskIdIsNull(Long parentTaskId) {
        return parentTaskId == null;
    }

    public boolean linkedTasksIdsIsNull(List<Long> linkedTasksIds) {
        return linkedTasksIds == null;
    }
}
