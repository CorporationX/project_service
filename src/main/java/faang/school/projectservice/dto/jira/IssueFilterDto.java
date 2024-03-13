package faang.school.projectservice.dto.jira;

import lombok.Data;

@Data
public class IssueFilterDto {
    private String projectKey;
    private String status;
    private String assignee;
}
