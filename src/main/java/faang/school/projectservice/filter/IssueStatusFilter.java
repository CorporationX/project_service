package faang.school.projectservice.filter;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class IssueStatusFilter implements IssueFilter {

    @Override
    public boolean isApplicable(IssueFilterDto filter) {
        return filter != null && filter.getStatus() != null;
    }

    @Override
    public String createJql(IssueFilterDto filter) {
        return "status = '%s'".formatted(filter.getStatus().getName());
    }
}
