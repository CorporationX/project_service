package faang.school.projectservice.dto.jira.issue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraGetMultipleIssuesResponse {
    private String expand;
    private int startAt;
    private int maxResults;
    private int total;
    private List<JiraGetIssueResponse> issues;
}
