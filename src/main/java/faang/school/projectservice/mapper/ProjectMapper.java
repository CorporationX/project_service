package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "ownerId", target = "ownerId")
    Project toEntity(ProjectDto projectDto);

    @Mapping(source = "ownerId", target = "ownerId")
    ProjectDto toDto(Project project);

}