package faang.school.projectservice.dto.jiratask.task;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraTransitionDto(
        @JsonProperty("id") String transitionId
) {
}
