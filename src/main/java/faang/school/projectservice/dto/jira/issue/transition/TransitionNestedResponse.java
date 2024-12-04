package faang.school.projectservice.dto.jira.issue.transition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record TransitionNestedResponse(

        @JsonProperty("id")
        String id,

        @JsonProperty("name")
        String name,

        @JsonProperty("to")
        TransitionTo to,

        @JsonProperty("hasScreen")
        boolean hasScreen,

        @JsonProperty("isGlobal")
        boolean isGlobal,

        @JsonProperty("isInitial")
        boolean isInitial,

        @JsonProperty("isAvailable")
        boolean isAvailable,

        @JsonProperty("isConditional")
        boolean isConditional,

        @JsonProperty("isLooped")
        boolean isLooped
) {
}
