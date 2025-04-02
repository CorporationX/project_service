package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraSearchResponse(
        @JsonProperty("issues") List<JiraTaskResponse> issues,
        @JsonProperty("total") int total,
        @JsonProperty("maxResults") int maxResults,
        @JsonProperty("startAt") int startAt
) {}
