package faang.school.projectservice.dto.jira.issue.create;

import faang.school.projectservice.dto.jira.adf.ADFDocument;
import faang.school.projectservice.dto.jira.issue.nested.Assignee;
import faang.school.projectservice.dto.jira.issue.nested.IssueTypeRef;
import faang.school.projectservice.dto.jira.issue.nested.Project;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@Data
public class CreateFields {

    @JsonProperty("project")
    private Project project;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("description")
    private ADFDocument description;

    @JsonProperty("issuetype")
    private IssueTypeRef issuetype;

    @JsonProperty("assignee")
    private Assignee assignee;

    @JsonProperty("labels")
    private List<String> labels;
}
