package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.description.JiraContentDto;

import java.util.List;

public record JiraDescriptionDto(

        @JsonProperty("type")
        String type,

        @JsonProperty("version")
        int version,

        @JsonProperty("content")
        List<JiraContentDto> content
) {
}
