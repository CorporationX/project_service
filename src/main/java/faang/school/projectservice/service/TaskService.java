package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.webhook.IssuePayloadFields;
import faang.school.projectservice.dto.jira.webhook.JiraUpdateIssuePayload;
import faang.school.projectservice.exceptions.jira.JiraNotFoundException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.task.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    public void updateTaskByJira(String issuekey, JiraUpdateIssuePayload payload) {
        Task persistTask = taskRepository.findByIssueKey(issuekey)
                .orElseThrow(() -> new JiraNotFoundException(String.format(issuekey)));

        IssuePayloadFields updatedFields = payload.getIssue().getFields();
        persistTask.setName(updatedFields.getSummary());
        persistTask.setDescription(updatedFields.getDescription());

        taskRepository.save(persistTask);
    }
}
