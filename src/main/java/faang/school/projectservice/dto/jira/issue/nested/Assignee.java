package faang.school.projectservice.dto.jira.issue.nested;

import org.codehaus.jackson.annotate.JsonProperty;

public record Assignee(
        @JsonProperty("accountId")
        String accountId
) {
}
