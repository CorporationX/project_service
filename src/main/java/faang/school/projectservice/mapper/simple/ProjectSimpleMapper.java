package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.ProjectSimpleDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectSimpleMapper {
    ProjectSimpleDto toDto(Project project);
    Project toEntity(ProjectSimpleDto projectDto);
}
