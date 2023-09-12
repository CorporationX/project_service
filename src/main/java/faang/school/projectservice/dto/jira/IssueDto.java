package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueDto {

    @Valid
    @NotNull(message = "Issue fields is required")
    private IssueFields fields;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IssueFields {

        @Valid
        @NotNull(message = "Project is required")
        private Project project;

        @NotBlank(message = "Summary is required")
        private String summary;

        private String description;

        @JsonProperty("duedate")
        private String dueDate;

        private Assignee assignee;

        @Valid
        @NotNull(message = "Issue type is required")
        @JsonProperty("issuetype")
        private IssueType issueType;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Project {

            @NotBlank(message = "Project key is required")
            private String key;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Assignee {
            private String id;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IssueType {

            @Pattern(message = "Issue type must be one of the following: Bug, Task, Story, Epic",
                    regexp = "^(Bug|Task|Story|Epic)$")
            private String name;

            private boolean subtask;
        }
    }
}
