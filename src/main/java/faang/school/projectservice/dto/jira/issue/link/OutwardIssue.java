package faang.school.projectservice.dto.jira.issue.link;

import org.codehaus.jackson.annotate.JsonProperty;

public record OutwardIssue(
        @JsonProperty("key")
        String key
) {
}
