package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class IssueStatusFilter implements IssueFilter {
    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return issueFilterDto != null && issueFilterDto.getStatus() != null
                && issueFilterDto.getStatus().getName() != null;
    }

    @Override
    public String createJql(IssueFilterDto issueFilterDto) {
        return "status = \"" + issueFilterDto.getStatus().getName() + "\"";
    }
}
