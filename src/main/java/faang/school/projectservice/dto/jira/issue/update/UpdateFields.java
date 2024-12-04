package faang.school.projectservice.dto.jira.issue.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.adf.ADFDocument;
import faang.school.projectservice.dto.jira.issue.nested.IssueTypeRef;
import faang.school.projectservice.dto.jira.issue.nested.Assignee;
import faang.school.projectservice.dto.jira.issue.nested.Parent;
import lombok.Data;

@Data
public class UpdateFields {

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("description")
    private ADFDocument description;

    @JsonProperty("assignee")
    private Assignee assignee;

    @JsonProperty("parent")
    private Parent parent;

    @JsonProperty("issuetype")
    private IssueTypeRef issuetype;
}
