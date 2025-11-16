package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(source = "name", target = "title")
    ProjectDto toProjectDto(Project project);

    @Mapping(source = "title", target = "name")
    Project toProject(ProjectDto projectDto);
}
