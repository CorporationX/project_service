package faang.school.projectservice.dto.jira.issue.transition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record TransitionsResponse(

        @JsonProperty("expand")
        String expand,

        @JsonProperty("transitions")
        List<TransitionNestedResponse> transitions
) {
}
