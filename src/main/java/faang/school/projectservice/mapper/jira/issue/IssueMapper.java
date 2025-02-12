package faang.school.projectservice.mapper.jira.issue;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.dto.jira.issue.IssueDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(source = "issueType.id", target = "typeId")
    @Mapping(source = "assignee.name", target = "assigneeName")
    IssueDto toDto(Issue issue);

    List<IssueDto> toDto(List<Issue> issues);

    default IssueInput toIssueInput(IssueDto issueDto) {
        IssueInputBuilder builder = new IssueInputBuilder();

        builder.setIssueTypeId(issueDto.getTypeId());
        builder.setSummary(issueDto.getSummary());

        String description = issueDto.getDescription() != null ? issueDto.getDescription() : "";
        String assigneeName = issueDto.getAssigneeName() != null ? issueDto.getAssigneeName() : "";

        builder.setDescription(description);
        builder.setAssigneeName(assigneeName);

        return builder.build();
    }

    default IssueInput toIssueInput(String projectKey, IssueDto issueDto) {
        IssueInputBuilder builder = new IssueInputBuilder();

        builder.setProjectKey(projectKey);
        builder.setIssueTypeId(issueDto.getTypeId());
        builder.setSummary(issueDto.getSummary());

        String description = issueDto.getDescription() != null ? issueDto.getDescription() : "";
        String assigneeName = issueDto.getAssigneeName() != null ? issueDto.getAssigneeName() : "";

        builder.setDescription(description);
        builder.setAssigneeName(assigneeName);

        return builder.build();
    }
}
