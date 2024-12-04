package faang.school.projectservice.dto.jira.issue.nested;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.adf.ADFDocument;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueFields {

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("description")
    private ADFDocument description;

    @JsonProperty("status")
    private JiraStatus status;

    @JsonProperty("assignee")
    private JiraUser assignee;

    @JsonProperty("issuetype")
    private IssueTypeRef issuetype;
}
