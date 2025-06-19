package faang.school.projectservice.dto.jira.issue.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.dto.jira.issue.Fields;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JiraCreateIssueRequest {
    private Fields fields;
}
