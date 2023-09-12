package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class IssueProjectFilter extends IssueAbstractFilter {

    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return Objects.nonNull(issueFilterDto.getProject());
    }

    @Override
    public StringBuilder apply(StringBuilder filter, IssueFilterDto issueFilterDto) {
        return concatFilter(filter, "project", issueFilterDto.getAssignee());
    }
}
