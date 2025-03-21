package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;

public class AssigneeFilter implements IssueFilter {
    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return issueFilterDto != null && issueFilterDto.getAssignee() != null;
    }

    @Override
    public String createJql(IssueFilterDto issueFilterDto) {
        return "transition = " + issueFilterDto.getAssignee().getAccountId();
    }
}
