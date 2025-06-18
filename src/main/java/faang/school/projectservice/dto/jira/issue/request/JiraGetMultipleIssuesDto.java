package faang.school.projectservice.dto.jira.issue.request;

import faang.school.projectservice.dto.jira.issue.JiraIssueFilterDto;
import lombok.Data;

@Data
public class JiraGetMultipleIssuesDto {
    Integer startAt;
    Integer maxResults;
    Integer limit;
    JiraIssueFilterDto jiraIssueFilterDto;
}
