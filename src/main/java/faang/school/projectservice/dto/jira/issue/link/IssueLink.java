package faang.school.projectservice.dto.jira.issue.link;

import lombok.Builder;
import org.codehaus.jackson.annotate.JsonProperty;

@Builder
public record IssueLink(
        @JsonProperty("add")
        IssueLinkDetails add
) {
}
