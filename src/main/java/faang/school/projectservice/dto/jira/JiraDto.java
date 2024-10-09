package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.validator.groups.ChangeStatusGroup;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.FilterGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import faang.school.projectservice.validator.groups.UpdateLinkGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JiraDto {
    @NotNull(groups = UpdateLinkGroup.class)
    @Valid
    private IssueLinkType type;
    @NotNull(groups = UpdateLinkGroup.class)
    @Valid
    private InwardIssue inwardIssue;
    @NotNull(groups = UpdateLinkGroup.class)
    @Valid
    private OutwardIssue outwardIssue;
    @NotNull(groups = CreateGroup.class)
    @Valid
    private Fields fields;
    private String id;
    @NotBlank(groups = {UpdateGroup.class, ChangeStatusGroup.class, FilterGroup.class})
    private String key;
    @NotBlank(groups = ChangeStatusGroup.class)
    private String status;
    private List<String> errorMessages;
    private Map<String, String> errors;
    private List<JiraIssue> issues;
    @Valid
    @NotEmpty(groups = ChangeStatusGroup.class)
    private List<JiraStatusDto> transitions;
    private String assignee;
    private String nameUser;

    @Data
    public class JiraIssue {
        private String key;
        private Fields fields;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Fields {
        @NotNull(groups = CreateGroup.class)
        @Valid
        private Project project;
        @NotNull(groups = CreateGroup.class)
        @Valid
        private IssueType issuetype;
        @NotBlank(groups = CreateGroup.class)
        private String summary;
        private String description;
        private Assignee assignee;
        private Parent parent;

        @Data
        public class Project {
            @NotBlank(groups = CreateGroup.class)
            private String key;
        }

        @Data
        public class IssueType {
            @NotBlank(groups = CreateGroup.class)
            private String name;
            private boolean subtask;
        }

        @Data
        public class Assignee {
            private String accountId;
        }

        @Data
        public class Parent {
            private String key;
        }
    }

    @Data
    public class IssueLinkType {
        @NotBlank(groups = UpdateLinkGroup.class)
        private String name;
    }

    @Data
    public class InwardIssue {
        @NotBlank(groups = UpdateLinkGroup.class)
        private String key;
    }

    @Data
    public class OutwardIssue {
        @NotBlank(groups = UpdateLinkGroup.class)
        private String key;
    }
}
