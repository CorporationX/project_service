package faang.school.projectservice.dto.jira.issue.link;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LinkType(
        @JsonProperty("name")
        String name
) {
}
