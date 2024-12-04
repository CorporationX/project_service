package faang.school.projectservice.dto.jira.issue.link;

import lombok.Builder;
import org.codehaus.jackson.annotate.JsonProperty;

@Builder
public record IssueLinkDetails(
        @JsonProperty("type")
        LinkType type,

        @JsonProperty("outwardIssue")
        OutwardIssue outwardIssue
) {
}
