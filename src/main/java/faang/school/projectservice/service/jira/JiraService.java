package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.dto.task.JiraCreateIssueDto;
import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class JiraService {
    private final JiraRestClient jiraRestClient;

    public String createIssueInJira(JiraCreateIssueDto jiraCreateIssueDto) {
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder("BJS2", jiraCreateIssueDto.issueType());
        issueInputBuilder.setSummary(jiraCreateIssueDto.summary());
        issueInputBuilder.setDescription(jiraCreateIssueDto.description());
        issueInputBuilder.setAssigneeName(jiraCreateIssueDto.username());
        issueInputBuilder.setPriorityId(1L);

        IssueInput issueInput = issueInputBuilder.build();
        BasicIssue issue = jiraRestClient.getIssueClient().createIssue(issueInput).claim();

        return issue.getKey();
    }

    public void updateIssueInJira(String jiraKey, Task task, String username) {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder()
                .setSummary(task.getName())
                .setFieldValue("parent", task.getParentTask().getJiraKey())
                .setAssigneeName(username)
                .setDescription(task.getDescription());

        issueClient.updateIssue(jiraKey, issueInputBuilder.build()).claim();

        log.info("Issue in Jira updated: {}", jiraKey);
    }
}
