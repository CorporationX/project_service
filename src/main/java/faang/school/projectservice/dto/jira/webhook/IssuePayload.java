package faang.school.projectservice.dto.jira.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssuePayload {

    @JsonProperty("id")
    @NotBlank(message = "Id cannot be blank")
    private String id;

    @JsonProperty("self")
    @NotBlank(message = "self cannot be null")
    private String self;

    @JsonProperty("key")
    @NotBlank(message = "Key cannot be null")
    private String key;

    @JsonProperty("fields")
    @NotNull
    private IssuePayloadFields fields;
}
