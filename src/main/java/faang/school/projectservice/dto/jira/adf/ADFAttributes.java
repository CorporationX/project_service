package faang.school.projectservice.dto.jira.adf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ADFAttributes {

    @JsonProperty("level")
    private Integer level;
}
