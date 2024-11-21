package faang.school.projectservice.filter.jirafilter;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class IssueStatusFilter implements IssueFilter {

    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return issueFilterDto.status() != null;
    }

    @Override
    public String getJql(IssueFilterDto filter) {
        String jqlFormat = " AND status = %s";
        return String.format(jqlFormat, filter.status());
    }
}
