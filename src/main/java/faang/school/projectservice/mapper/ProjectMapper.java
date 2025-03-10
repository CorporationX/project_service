package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {
  @Mapping(source = "name", target = "title")
  ProjectDto toDto(Project user);
}
