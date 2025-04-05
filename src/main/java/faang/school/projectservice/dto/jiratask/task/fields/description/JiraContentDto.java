package faang.school.projectservice.dto.jiratask.task.fields.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.description.content.JiraTextContentDto;
import lombok.Builder;

import java.util.List;

@Builder
public record JiraContentDto(

        @JsonProperty("type")
        String type,

        @JsonProperty("content")
        List<JiraTextContentDto> content
) {
}
