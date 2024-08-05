package faang.school.projectservice.validator.task;

import faang.school.projectservice.exception.task.TaskExceptionHandler;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskValidator {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void validateTeamMember(Long userId, Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        teamMemberRepository.findByUserIdAndProjectId(userId, project.getId());
    }

    public Task validateTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() ->
                new TaskExceptionHandler("The task does not exist!"));
    }

    public boolean isNull(Object obj) {
        return obj == null;
    }
}
