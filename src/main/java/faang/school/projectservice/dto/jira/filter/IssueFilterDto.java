package faang.school.projectservice.dto.jira.filter;

import faang.school.projectservice.dto.jira.request.IssueStatusRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueFilterDto {
    private AssigneeDto assignee;
    private IssueStatusRequestDto status;
}
