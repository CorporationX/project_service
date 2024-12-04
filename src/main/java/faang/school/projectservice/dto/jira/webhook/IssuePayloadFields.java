package faang.school.projectservice.dto.jira.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssuePayloadFields {

    @JsonProperty("summary")
    @NotBlank(message = "Summary cannot be null")
    private String summary;

    @JsonProperty("created")
    private String created;

    @JsonProperty("description")
    private String description;

    @JsonProperty("labels")
    private List<String> labels;
}
