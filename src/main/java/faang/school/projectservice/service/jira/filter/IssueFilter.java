package faang.school.projectservice.service.jira.filter;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.dto.jira.IssueFilterDto;

import java.util.stream.Stream;

public interface IssueFilter {

    boolean isAcceptable(IssueFilterDto filters);

    Stream<Issue> apply(Stream<Issue> issues, IssueFilterDto filters);
}
