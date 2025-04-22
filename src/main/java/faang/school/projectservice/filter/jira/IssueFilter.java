package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;

public interface IssueFilter {
    boolean isApplicable(IssueFilterDto issueFilterDto);

    String createJql(IssueFilterDto issueFilterDto);
}
