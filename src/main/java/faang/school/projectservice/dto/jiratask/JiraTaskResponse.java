package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record JiraTaskResponse(
        @JsonProperty("id")
        String id,

        @JsonProperty("key")
        String key,

        @JsonProperty("self")
        String selfUrl
) {
}
