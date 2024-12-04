package faang.school.projectservice.dto.jira.issue.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.issue.IssueResponse;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraSearchResponse {

    @JsonProperty("maxResults")
    private Integer maxResults;

    @JsonProperty("startAt")
    private Integer startAt;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("issues")
    private List<IssueResponse> issues;
}
