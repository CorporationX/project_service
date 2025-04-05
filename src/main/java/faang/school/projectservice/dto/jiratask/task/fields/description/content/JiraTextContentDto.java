package faang.school.projectservice.dto.jiratask.task.fields.description.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record JiraTextContentDto(

        @JsonProperty("text")
        String text,

        @JsonProperty("type")
        String type
) {
}
