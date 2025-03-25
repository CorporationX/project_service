package faang.school.projectservice.dto.jira.filter;

import faang.school.projectservice.dto.jira.request.IssueStatusRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IssueFilterDto {
    private AssigneeDto assignee;
    private IssueStatusRequestDto status;
}
