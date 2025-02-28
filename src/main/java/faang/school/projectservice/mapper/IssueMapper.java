package faang.school.projectservice.mapper;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.dto.issue.IssueDto;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(source = "issueType.id", target = "typeId")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "dueDate", target = "dueDate", qualifiedByName = "toLocalDateTime")
    IssueDto toIssueDto(Issue issue);

    Iterable<IssueDto> toIterableIssueDto(Iterable<Issue> issues);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(DateTime dateTime) {
        return dateTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
