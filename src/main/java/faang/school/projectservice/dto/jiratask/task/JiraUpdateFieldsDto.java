package faang.school.projectservice.dto.jiratask.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.JiraAssigneeDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraDescriptionDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraParentDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraPriorityDto;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record JiraUpdateFieldsDto(

        @JsonProperty("summary")
        String summary,

        @JsonProperty("description")
        JiraDescriptionDto description,

        @JsonProperty("assignee")
        JiraAssigneeDto assignee,

        @JsonProperty("priority")
        JiraPriorityDto priority,

        @JsonProperty("labels")
        List<String> labels,

        @JsonProperty("duedate")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
        String dueDate,

        @JsonProperty("parent")
        JiraParentDto parent
) {
}
