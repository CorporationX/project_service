package faang.school.projectservice.filter;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class IssueAssigneeFilter implements IssueFilter {

    @Override
    public boolean isApplicable(IssueFilterDto filter) {
        return filter != null && filter.getAssignee() != null;
    }

    @Override
    public String createJql(IssueFilterDto filter) {
        return "transition = '%s'".formatted(filter.getAssignee().getAccountId());
    }
}
