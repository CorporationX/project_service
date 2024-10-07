package faang.school.projectservice.filter;

import faang.school.projectservice.dto.jira.IssueFilterDto;

public interface IssueFilter {

    boolean isApplicable(IssueFilterDto filter);

    String createJql(IssueFilterDto filter);
}
