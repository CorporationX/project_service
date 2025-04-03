package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraParentDto(
        @JsonProperty("key") String key
) {
}
