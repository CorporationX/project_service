package faang.school.projectservice.mapper;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    IssueDto toIssueDto(Issue issue);

    List<IssueDto> toIssueDtos(List<Issue> issues);

    IssueType toIssueType(IssueTypeDto issueTypeDto);
}
