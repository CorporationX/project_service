package faang.school.projectservice.dto.jira.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutwardIssue {
    private Long id;
    private String key;
}
