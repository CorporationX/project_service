package faang.school.projectservice.mapper;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.dto.issue.IssueDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(source = "issueType.id", target = "typeId")
    @Mapping(source = "status.id", target = "statusId")
    IssueDto toIssueDto(Issue issue);

    Iterable<IssueDto> toIterableIssueDto(Iterable<Issue> issues);
}
