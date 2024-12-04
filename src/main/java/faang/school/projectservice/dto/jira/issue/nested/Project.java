package faang.school.projectservice.dto.jira.issue.nested;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Project(
        @JsonProperty("key")
        String key
) {
}
