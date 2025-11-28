package faang.school.projectservice.integration.jira.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraIssueResponse {
    
    private String id;
    private String key;
    private String self;
    
    @JsonProperty("fields")
    private Fields fields;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fields {
        private String summary;
        private String description;
        private Status status;
        private Assignee assignee;
        private String duedate;
        private Parent parent;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Assignee {
        @JsonProperty("displayName")
        private String displayName;
        private String emailAddress;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parent {
        private String key;
    }
}

