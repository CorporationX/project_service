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
public class JiraIssueRequest {
    
    @JsonProperty("fields")
    private Fields fields;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fields {
        private Project project;
        private IssueType issuetype;
        private String summary;
        private String description;
        private Assignee assignee;
        private String duedate;
        private Parent parent;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Project {
        private String key;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssueType {
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Assignee {
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parent {
        private String key;
    }
}

