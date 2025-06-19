package faang.school.projectservice.dto.jira.issue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.projectservice.dto.jira.issue.Fields;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraUpdateIssueResponse {
    private String id;
    private String key;
    private String self;
    private String summary;
    private String status;
    private String updated;
    private Fields fields;
}
