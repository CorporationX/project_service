package faang.school.projectservice.integration.jira.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraIssueLinkRequest {
    
    @JsonProperty("type")
    private LinkType type;
    
    @JsonProperty("inwardIssue")
    private InwardIssue inwardIssue;
    
    @JsonProperty("outwardIssue")
    private OutwardIssue outwardIssue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkType {
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InwardIssue {
        private String key;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutwardIssue {
        private String key;
    }
}

