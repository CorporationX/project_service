package faang.school.projectservice.dto.jira.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JiraCreateTaskDto {

    @NotBlank
    @JsonProperty("summary")
    private String summary;

    @NotBlank
    @JsonProperty("issueType")
    private String issueType;

    @NotBlank
    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("assigneeId")
    private String assigneeId;

    @JsonProperty("parentKey")
    private String parentKey;

    @JsonProperty("fields")
    public Fields getFields() {
        Fields fields = new Fields();
        fields.setSummary(this.summary);

        if (this.issueType != null) {
            fields.setIssuetype(new IssueType(this.issueType));
        }

        if (this.projectId != null) {
            fields.setProject(new Project(this.projectId));
        }

        if (this.assigneeId != null) {
            fields.setAssignee(new Assignee(this.assigneeId));
        }

        if (this.parentKey != null) {
            fields.setParent(new Parent(this.parentKey));
        }

        return fields;
    }

    // Внутренние классы
    @Data
    public static class Fields {
        private String summary;
        private IssueType issuetype;
        private Project project;
        private Assignee assignee;
        private Parent parent;
    }

    @Data
    public static class IssueType {
        private final String id;
        public IssueType(String id) {
            this.id = id;
        }
    }

    @Data
    public static class Project {
        private final String id;
        public Project(String id) {
            this.id = id;
        }
    }

    @Data
    public static class Assignee {
        private final String id;
        public Assignee(String id) {
            this.id = id;
        }
    }

    @Data
    public static class Parent {
        private final String key;
        public Parent(String key) {
            this.key = key;
        }
    }

    // Геттеры только для чтения (исключены из сериализации)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getSummary() {
        return summary;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getIssueType() {
        return issueType;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getAssigneeId() {
        return assigneeId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getParentKey() {
        return parentKey;
    }
}