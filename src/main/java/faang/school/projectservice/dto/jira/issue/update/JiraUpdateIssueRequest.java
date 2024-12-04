package faang.school.projectservice.dto.jira.issue.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraUpdateIssueRequest(
        @JsonProperty("fields")
        UpdateFields fields,

        @JsonProperty("update")
        UpdateData update
) {
}
