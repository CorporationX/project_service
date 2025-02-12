package faang.school.projectservice.dto.jira.request;

import lombok.Data;

@Data
public class JiraIssueRequest {
    private Fields fields;

    @Data
    public static class Fields {
        private Project project;
        private String summary;
        private String description;
        private IssueType issuetype;

        @Data
        public static class Project {
            private String key;
        }

        @Data
        public static class IssueType {
            private String name;
        }
    }
}