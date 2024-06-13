package faang.school.projectservice.dto.jira;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueFilterDto {
    private Long statusId;
    private String assigneeId;
}
