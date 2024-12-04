package faang.school.projectservice.dto.jira.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraUpdateIssuePayload {

    @JsonProperty("id")
    private String id;

    @JsonProperty("timestamp")
    @NotNull(message = "Timestamp cannot be null")
    private Long timestamp;

    @JsonProperty("webhookEvent")
    @NotNull(message = "Webhook event cannot be null")
    private String webhookEvent;

    @JsonProperty("issue")
    @NotNull(message = "Issue cannot be null")
    private IssuePayload issue;
}
