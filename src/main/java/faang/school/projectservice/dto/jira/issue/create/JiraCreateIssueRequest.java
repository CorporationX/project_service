package faang.school.projectservice.dto.jira.issue.create;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraCreateIssueRequest(
        @JsonProperty("fields")
        CreateFields fields
) {
}
