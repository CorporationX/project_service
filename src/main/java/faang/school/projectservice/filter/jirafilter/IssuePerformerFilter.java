package faang.school.projectservice.filter.jirafilter;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class IssuePerformerFilter implements IssueFilter {

    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return issueFilterDto.assignee() != null;
    }

    @Override
    public String getJql(IssueFilterDto filter) {
        String jqlFormat = " AND assignee = %s";
        return String.format(jqlFormat, filter.assignee());
    }
}
