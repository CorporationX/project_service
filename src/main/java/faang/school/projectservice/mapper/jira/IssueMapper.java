package faang.school.projectservice.mapper.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.dto.jira.IssueDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {
    IssueDto toDto(Issue issue);

    Issue toEntity(IssueDto issueDto);

    List<IssueDto> toDto(List<Issue> issues);
}