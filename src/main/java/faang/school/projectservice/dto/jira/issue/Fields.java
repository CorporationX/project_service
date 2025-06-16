package faang.school.projectservice.dto.jira.issue;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fields {
    // Основные поля
    private String summary;
    private String duedate; // Формат "YYYY-MM-DD"
    private List<String> labels;

    // Простые объекты с id (вложенные классы)
    @Data
    public static class Assignee {
        private String id;
    }
    private Assignee assignee;

    @Data
    public static class Reporter {
        private String id;
    }
    private Reporter reporter;

    @Data
    public static class Security {
        private String id;
    }
    private Security security;

    @Data
    public static class Component {
        private String id;
    }
    private List<Component> components;

    @Data
    public static class FixVersion {
        private String id;
    }
    private List<FixVersion> fixVersions;

    @Data
    public static class IssueType {
        private String id;
    }
    private IssueType issuetype;

    @Data
    public static class Priority {
        private String id;
    }
    private Priority priority;

    @Data
    public static class Project {
        private String id;
        private String key;
    }
    private Project project;

    @Data
    public static class Version {
        private String id;
    }
    private List<Version> versions;

    @Data
    public static class Parent {
        private String key;
    }
    private Parent parent;

    // Специальные типы данных
    @Data
    public static class TimeTracking {
        private String originalEstimate;
        private String remainingEstimate;
    }
    private TimeTracking timetracking;

    @Data
    public static class ColorField {
        private String value;
    }
    private ColorField customfield_80000;

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
    private Document description;
    private Document environment;
    private Document customfield_40000;
    private Document customfield_50000;

    // Кастомные поля (примеры)
    private String customfield_10000;
    private Object customfield_20000;
    private List<String> customfield_30000;
    private String customfield_60000;
    private List<String> customfield_70000;
}