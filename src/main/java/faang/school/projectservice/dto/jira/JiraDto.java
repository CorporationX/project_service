package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.validator.groups.ChangeStatusGroup;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.FilterGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import faang.school.projectservice.validator.groups.UpdateLinkGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JiraDto {
    private IssueLinkType type;
    private InwardIssue inwardIssue;
    private OutwardIssue outwardIssue;
    private Fields fields;
    private String id;
    @NotNull(groups = {UpdateGroup.class, ChangeStatusGroup.class, FilterGroup.class})
    @NotEmpty(groups = {UpdateGroup.class, ChangeStatusGroup.class, FilterGroup.class})
    @NotBlank(groups = {UpdateGroup.class, ChangeStatusGroup.class, FilterGroup.class})
    private String key;
    @NotNull(groups = ChangeStatusGroup.class)
    @NotEmpty(groups = ChangeStatusGroup.class)
    @NotBlank(groups = ChangeStatusGroup.class)
    private String status;
    private List<String> errorMessages;
    private Map<String, String> errors;
    private List<JiraIssue> issues;
    private List<JiraTransitions> transitions;
    private String assignee;
    private String nameUser;

    @Data
    public static class JiraIssue {
        private String key;
        private Fields fields;
    }

    @Data
    public static class JiraTransitions {
        private String id;
        private String name;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fields {
        private Project project;
        private IssueType issuetype;
        @NotNull(groups = CreateGroup.class)
        @NotEmpty(groups = CreateGroup.class)
        @NotBlank(groups = CreateGroup.class)
        private String summary;
        private String description;
        private Assignee assignee;
        private Parent parent;

        @Data
        public static class Project {
            @NotNull(groups = CreateGroup.class)
            @NotEmpty(groups = CreateGroup.class)
            @NotBlank(groups = CreateGroup.class)
            private String key;
        }

        @Data
        public static class IssueType {
            @NotNull(groups = CreateGroup.class)
            @NotEmpty(groups = CreateGroup.class)
            @NotBlank(groups = CreateGroup.class)
            private String name;
            private boolean subtask;
        }

        @Data
        public static class Assignee {
            private String accountId;
        }

        @Data
        public static class Parent {
            private String key;
        }
    }

    @Data
    public static class IssueLinkType {
        @NotEmpty(groups = UpdateLinkGroup.class)
        @NotBlank(groups = UpdateLinkGroup.class)
        @NotNull(groups = UpdateLinkGroup.class)
        private String name;
    }

    @Data
    public static class InwardIssue {
        @NotEmpty(groups = UpdateLinkGroup.class)
        @NotBlank(groups = UpdateLinkGroup.class)
        @NotNull(groups = UpdateLinkGroup.class)
        private String key;
    }

    @Data
    public static class OutwardIssue {
        @NotEmpty(groups = UpdateLinkGroup.class)
        @NotBlank(groups = UpdateLinkGroup.class)
        @NotNull(groups = UpdateLinkGroup.class)
        private String key;
    }
}
