package faang.school.projectservice.dto.jiratask.task.fields.description.content;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraTextContentDto(
        @JsonProperty("text") String text,
        @JsonProperty("type") String type
) {
}
