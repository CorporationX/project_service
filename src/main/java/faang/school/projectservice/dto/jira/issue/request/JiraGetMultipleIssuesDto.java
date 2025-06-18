package faang.school.projectservice.dto.jira.issue.request;

import faang.school.projectservice.dto.jira.issue.JiraIssueFilterDto;
import lombok.Data;

@Data
public class JiraGetMultipleIssuesDto {
    public Integer startAt;
    public Integer maxResults;
    public Integer limit;
    public JiraIssueFilterDto jiraIssueFilterDto;
}
