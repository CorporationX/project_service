package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.description.JiraContentDto;
import lombok.Builder;

import java.util.List;

@Builder
public record JiraDescriptionDto(

        @JsonProperty("type")
        String type,

        @JsonProperty("version")
        int version,

        @JsonProperty("content")
        List<JiraContentDto> content
) {
}
