package faang.school.projectservice.dto.jira.issue;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fields {
    private Assignee assignee;
    private List<Component> components;

    private String customfield_10000;
    private Object customfield_20000;
    private List<String> customfield_30000;
    private Document customfield_40000;
    private Document customfield_50000;
    private String customfield_60000;
    private List<String> customfield_70000;
    private ColorField customfield_80000;

    private Document description;
    private String duedate; // Формат "YYYY-MM-DD"
    private Document environment;
    private List<FixVersion> fixVersions;
    private IssueType issuetype;
    private List<String> labels;
    private Parent parent;
    private Priority priority;
    private Project project;
    private Reporter reporter;
    private Security security;
    private String summary;
    private TimeTracking timetracking;
    private List<Version> versions;

    @Data
    public static class Assignee {
        private String id;
    }

    @Data
    public static class Component {
        private String id;
    }

    @Data
    public static class Reporter {
        private String id;
    }

    @Data
    public static class Security {
        private String id;
    }

    @Data
    public static class FixVersion {
        private String id;
    }

    @Data
    public static class IssueType {
        private String id;
    }

    @Data
    public static class Priority {
        private String id;
    }

    @Data
    public static class Project {
        private String id;
        private String key;
    }

    @Data
    public static class Version {
        private String id;
    }

    @Data
    public static class Parent {
        private String key;
    }

    @Data
    public static class TimeTracking {
        private String originalEstimate;
        private String remainingEstimate;
    }

    @Data
    public static class ColorField {
        private String value;
    }

    @Data
    public static class Document {
        private String type = "doc";
        private int version = 1;
        private List<Content> content;

        @Data
        public static class Content {
            private String type;
            private List<ContentItem> content;

            @Data
            public static class ContentItem {
                private String type;
                private String text;
            }
        }
    }
}