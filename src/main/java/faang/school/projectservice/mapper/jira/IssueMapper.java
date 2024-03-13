package faang.school.projectservice.mapper.jira;


import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.model.jira.Issue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(target = "fields.project.key", source = "projectKey")
    @Mapping(target = "fields.duedate", source = "duedate")
    @Mapping(target = "fields.summary", source = "summary")
    @Mapping(target = "fields.issuetype.id", source = "issueTypeId")
    @Mapping(target = "fields.description", source = "description")
    @Mapping(target = "fields.assignee.accountId", source = "assigneeId")
    @Mapping(target = "fields.parent.id", source = "parentId")
//    @Mapping(target = "fields.subtasks", source = ".", qualifiedByName = "getSubtasks")
    Issue toEntity(IssueDto issueDto);

    IssueDto toDto(Issue issue);
//    @Named("getSubtasks")
//    default List<Issue> getSubtasks(IssueDto issueDto) {
//        return issueDto.getSubtaskIds() != null ? issueDto.getSubtaskIds().stream()
//                .map(id -> Issue.builder().id(id).build())
//                .toList() : Collections.emptyList();
//    }


}
