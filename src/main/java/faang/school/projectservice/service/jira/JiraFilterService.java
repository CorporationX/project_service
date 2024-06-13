package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.dto.jira.IssueFilterDto;

import java.util.stream.Stream;

public interface JiraFilterService {
    Stream<Issue> applyAll(Stream<Issue> issues, IssueFilterDto issueFilterDto);
}
