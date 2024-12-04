package faang.school.projectservice.dto.jira.issue.nested;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Parent(
        @JsonProperty("key")
        String key
) {
}
