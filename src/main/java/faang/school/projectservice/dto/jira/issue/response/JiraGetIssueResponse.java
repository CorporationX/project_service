package faang.school.projectservice.dto.jira.issue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraGetIssueResponse {
    private String expand;
    private String id;
    private String self;
    private String key;
    private Fields fields;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fields {
        private String summary;
        private String description;
        private Issuetype issuetype;
        private Project project;
        private Status status;
        private User assignee;
        private User reporter;
        private Resolution resolution;
        private Priority priority;
        private List<String> labels;
        private Timetracking timetracking;
        private List<Component> components;
        private List<IssueLink> issuelinks;
        private List<Attachment> attachment;
        private CommentList comment;
        private User creator;
        private String created;
        private String updated;
        private String duedate;

        private Object customfield_10000;
        private Object customfield_10001;
    }

    @Data
    public static class Issuetype {
        private String id;
        private String name;
        private String description;
        private boolean subtask;
    }

    @Data
    public static class Project {
        private String id;
        private String key;
        private String name;
        private Map<String, String> avatarUrls;
    }

    @Data
    public static class Status {
        private String id;
        private String name;
        private String description;
        private StatusCategory statusCategory;
    }

    @Data
    public static class StatusCategory {
        private String id;
        private String key;
        private String name;
    }

    @Data
    public static class User {
        private String accountId;
        private String displayName;
        private String emailAddress;
        private boolean active;
        private String timeZone;
        private Map<String, String> avatarUrls;
    }

    @Data
    public static class Resolution {
        private String id;
        private String name;
        private String description;
    }

    @Data
    public static class Priority {
        private String id;
        private String name;
        private String iconUrl;
    }

    @Data
    public static class Timetracking {
        private String originalEstimate;
        private String remainingEstimate;
        private String timeSpent;
        private long originalEstimateSeconds;
        private long remainingEstimateSeconds;
        private long timeSpentSeconds;
    }

    @Data
    public static class Component {
        private String id;
        private String name;
        private String description;
    }

    @Data
    public static class IssueLink {
        private String id;
        private LinkType type;
        private LinkedIssue outwardIssue;
        private LinkedIssue inwardIssue;
    }

    @Data
    public static class LinkType {
        private String id;
        private String name;
        private String inward;
        private String outward;
    }

    @Data
    public static class LinkedIssue {
        private String id;
        private String key;
        private String self;
        private Map<String, Object> fields;
    }

    @Data
    public static class Attachment {
        private String id;
        private String filename;
        private User author;
        private String created;
        private long size;
        private String mimeType;
        private String content;
    }

    @Data
    public static class CommentList {
        private List<Comment> comments;
        private int maxResults;
        private int total;
        private int startAt;
    }

    @Data
    public static class Comment {
        private String id;
        private User author;
        private String body;
        private User updateAuthor;
        private String created;
        private String updated;
        private Visibility visibility;
    }

    @Data
    public static class Visibility {
        private String type;
        private String value;
    }
}