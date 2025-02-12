package faang.school.projectservice.dto.jira.response;

import lombok.Data;

import java.util.List;

@Data
public class JiraSearchResponse {
    private int total;
    private List<Issue> issues;

    @Data
    public static class Issue {
        private String id;
        private String key;
        private Fields fields;

        @Data
        public static class Fields {
            private String summary;
            private String status;
            private String assignee;
        }
    }
}