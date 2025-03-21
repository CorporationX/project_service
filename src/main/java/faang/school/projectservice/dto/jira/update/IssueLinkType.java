package faang.school.projectservice.dto.jira.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueLinkType {
    private Long id;
    private String name;
    private String inward;
    private String outward;
}
