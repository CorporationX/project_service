package faang.school.projectservice.mapper.jira;

import faang.school.projectservice.dto.jira.JiraProjectDto;
import faang.school.projectservice.model.jira.JiraProject;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface JiraProjectMapper {

    JiraProject toEntity(JiraProjectDto jiraProjectDto);

    JiraProjectDto toDto(JiraProject jiraProject);
}
