package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class AssigneeFilter implements IssueFilter {
    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return issueFilterDto != null && issueFilterDto.getAssignee() != null
                && issueFilterDto.getAssignee().getName() != null;
    }

    @Override
    public String createJql(IssueFilterDto issueFilterDto) {
        return "transition = " + issueFilterDto.getAssignee().getName();
    }
}
