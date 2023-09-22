package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class IssueAssigneeFilter extends IssueAbstractFilter {

    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return Objects.nonNull(issueFilterDto.getAssignee());
    }

    @Override
    public StringBuilder apply(StringBuilder filter, IssueFilterDto issueFilterDto) {
        return concatFilter(filter, "assignee", issueFilterDto.getAssignee());
    }
}
