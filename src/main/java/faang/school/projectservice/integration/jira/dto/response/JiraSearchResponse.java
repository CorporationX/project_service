package faang.school.projectservice.integration.jira.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraSearchResponse {
    
    @JsonProperty("issues")
    private List<JiraIssueResponse> issues;
    
    @JsonProperty("total")
    private int total;
    
    @JsonProperty("maxResults")
    private int maxResults;
    
    @JsonProperty("startAt")
    private int startAt;
}

