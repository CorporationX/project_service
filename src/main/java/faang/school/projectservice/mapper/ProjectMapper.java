package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.model.Project;
import org.apache.catalina.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "string", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectDto toDto(Project project);
    Project toEntity(ProjectDto projectDto);
}
