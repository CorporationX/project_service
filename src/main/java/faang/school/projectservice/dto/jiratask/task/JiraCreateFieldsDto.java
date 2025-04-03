package faang.school.projectservice.dto.jiratask.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.JiraAssigneeDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraDescriptionDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraIssueTypeDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraParentDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraPriorityDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraProjectDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record JiraCreateFieldsDto(
        @JsonProperty("project") @Valid JiraProjectDto project,
        @JsonProperty("summary") @NotBlank String summary,
        @JsonProperty("issuetype") @Valid JiraIssueTypeDto issueType,

        @JsonProperty("description") JiraDescriptionDto description,
        @JsonProperty("assignee") JiraAssigneeDto assignee,
        @JsonProperty("priority") JiraPriorityDto priority,
        @JsonProperty("labels") List<@NotBlank String> labels,
        @JsonProperty("parent") JiraParentDto parent,
        @JsonProperty("duedate") @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String dueDate
) {
}
