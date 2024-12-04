package faang.school.projectservice.dto.jira.adf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ADFBlock {

    @JsonProperty("type")
    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("attrs")
    private ADFAttributes attrs;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("content")
    private List<ADFBlock> content;

    @JsonProperty("text")
    private String text;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("marks")
    private List<ADFMark> marks;
}
