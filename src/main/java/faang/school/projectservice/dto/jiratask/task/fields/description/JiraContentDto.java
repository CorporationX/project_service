package faang.school.projectservice.dto.jiratask.task.fields.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.description.content.JiraTextContentDto;
import java.util.List;

public record JiraContentDto(
        @JsonProperty("type") String type,
        @JsonProperty("content") List<JiraTextContentDto> content
) {
}
