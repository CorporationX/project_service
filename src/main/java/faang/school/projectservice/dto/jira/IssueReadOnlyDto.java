package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueReadOnlyDto {

    private String id;

    private String key;

    private String self;

    private IssueFields fields;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IssueFields {

        private Project project;

        private String summary;

        private String description;

        @JsonProperty("duedate")
        private String dueDate;

        private Assignee assignee;

        @JsonProperty("issuetype")
        private IssueType issueType;

        private IssueStatus status;

        @JsonProperty("issuelinks")
        private List<IssueLink> issueLinks;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Project {

            private String id;

            private String key;

            private String name;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Assignee {

            @JsonProperty("accountId")
            private String id;

            @JsonProperty("displayName")
            private String name;

            @JsonProperty("emailAddress")
            private String email;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IssueType {

            private String name;

            private boolean subtask;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IssueStatus {

            private String name;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IssueLink {

            private Type type;

            private IssueReadOnlyDto inwardIssue;

            private IssueReadOnlyDto outwardIssue;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Type {

                private String name;

                private String inward;

                private String outward;
            }
        }
    }
}
