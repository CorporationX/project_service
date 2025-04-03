package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.JiraTransitionDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraStatusUpdateRequest(
        @JsonProperty("transition") JiraTransitionDto transition
) {
}
