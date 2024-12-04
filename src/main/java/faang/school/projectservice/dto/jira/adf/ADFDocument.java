package faang.school.projectservice.dto.jira.adf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADFDocument {

    @JsonProperty("type")
    private String type = "doc";

    @JsonProperty("version")
    private int version = 1;

    @JsonProperty("content")
    private List<ADFBlock> content;

    public ADFDocument(List<ADFBlock> content) {
        this.content = content;
    }
}
