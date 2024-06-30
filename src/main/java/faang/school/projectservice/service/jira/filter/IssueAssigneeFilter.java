package faang.school.projectservice.service.jira.filter;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class IssueAssigneeFilter implements IssueFilter {

    @Override
    public boolean isAcceptable(IssueFilterDto filters) {
        return filters.getAssigneeId() != null;
    }

    @Override
    public Stream<Issue> apply(Stream<Issue> issues, IssueFilterDto filters) {
        return issues.filter(issue -> Objects.requireNonNull(issue.getAssignee()).getAccountId().equals(String.valueOf(filters.getAssigneeId())));
    }
}
