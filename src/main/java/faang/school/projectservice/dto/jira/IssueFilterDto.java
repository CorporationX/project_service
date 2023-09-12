package faang.school.projectservice.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueFilterDto {

    private String project;

    private String assignee;

    private String status;
}
