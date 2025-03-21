package faang.school.projectservice.dto.jira.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InwardIssue {
    private Long id;
    private String key;
}
