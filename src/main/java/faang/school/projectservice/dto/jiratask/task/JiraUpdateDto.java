package faang.school.projectservice.dto.jiratask.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.JiraIssueLinkOperationDto;

import java.util.List;

public record JiraUpdateDto(

        @JsonProperty("issuelinks")
        List<JiraIssueLinkOperationDto> linkedTasks
) {
}
