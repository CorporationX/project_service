package faang.school.projectservice.mapper;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import faang.school.projectservice.dto.issue.IssueDto;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(source = "issueType", target = "typeId", qualifiedByName = "typeToTypeId")
    @Mapping(source = "dueDate", target = "dueDate", qualifiedByName = "toLocalDateTime")
    IssueDto toIssueDto(Issue issue);

    Iterable<IssueDto> toIssueDtoIterable(Iterable<Issue> issues);

    @Named("typeToTypeId")
    default Long typeToTypeId(IssueType issueType){
        return issueType.getId();
    }

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(DateTime dateTime){
        return dateTime.toLocalDateTime();
    }
}
