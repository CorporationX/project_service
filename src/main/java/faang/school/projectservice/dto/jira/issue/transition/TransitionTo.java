package faang.school.projectservice.dto.jira.issue.transition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransitionTo {

    @JsonProperty("self")
    private String self;

    @JsonProperty("description")
    private String description;

    @JsonProperty("iconUrl")
    private String iconUrl;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;

    @JsonProperty("statusCategory")
    private StatusCategory statusCategory;
}
