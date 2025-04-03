package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.JiraFieldsResponseDto;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraTaskResponse(
        @JsonProperty("id") String id,
        @JsonProperty("key") String key,
        @JsonProperty("self") String selfUrl,
        @JsonProperty("fields") JiraFieldsResponseDto fields
) {
}
