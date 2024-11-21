package faang.school.projectservice.filter.jirafilter;

import faang.school.projectservice.dto.jira.IssueFilterDto;

public interface IssueFilter {

    boolean isApplicable(IssueFilterDto issueFilterDto);

    String getJql(IssueFilterDto filter);
}
