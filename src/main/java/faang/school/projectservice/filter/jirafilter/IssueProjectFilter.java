package faang.school.projectservice.filter.jirafilter;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class IssueProjectFilter implements IssueFilter {

    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return true;
    }

    @Override
    public String getJql(IssueFilterDto filter) {
        String jqlFormat = "project = %s";
        return String.format(jqlFormat, filter.projectKey());
    }
}
