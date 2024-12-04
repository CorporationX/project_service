package faang.school.projectservice.dto.jira.issue.transition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusCategory {

    @JsonProperty("self")
    private String self;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("key")
    private String key;

    @JsonProperty("colorName")
    private String colorName;

    @JsonProperty("name")
    private String name;
}
