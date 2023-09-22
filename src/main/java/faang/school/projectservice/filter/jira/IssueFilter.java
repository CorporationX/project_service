package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public interface IssueFilter {

    boolean isApplicable(IssueFilterDto issueFilterDto);

    StringBuilder apply(StringBuilder filter, IssueFilterDto issueFilterDto);
}
