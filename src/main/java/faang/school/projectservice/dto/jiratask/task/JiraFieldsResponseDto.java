package faang.school.projectservice.dto.jiratask.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.JiraDescriptionDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraParentDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraPriorityDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraStatusDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraUserDto;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record JiraFieldsResponseDto(
        @JsonProperty("summary") String summary,
        @JsonProperty("description") JiraDescriptionDto description,
        @JsonProperty("status") JiraStatusDto status,
        @JsonProperty("assignee") JiraUserDto assignee,
        @JsonProperty("priority") JiraPriorityDto priority,
        @JsonProperty("labels") List<String> labels,
        @JsonProperty("duedate") @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String dueDate,
        @JsonProperty("parent") JiraParentDto parent,
        @JsonProperty("created") String created
) {
}
