package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueDto {
    private Long id;
    private String key;
    private Fields fields;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fields {
        private String summary;
        private String description;
        private IssueTypeDto issuetype;
        private Parent parent;
        private Assignee assignee;
        private Project project;
        private Status status;
    }

    @Data
    public static class IssueTypeDto {
        private Long id;
        private String description;
        private String name;
        private boolean subtask;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private String id;
        private String description;
        private String name;
    }

    @Data
    public static class Project {
        private String key;
    }
}

