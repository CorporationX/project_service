package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BaseProjectMapper.class)
public interface ProjectMapper  {

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "children", source = "children", qualifiedByName = "mapProjectsToIds")
    @Mapping(target = "parentProject", source = "parentProject.id")
    @Mapping(target = "stages", source = "stages", qualifiedByName = "mapStagesToIds")
    @Mapping(target = "tasks", source = "tasks", qualifiedByName = "mapTasksToIds")
    ProjectDto toDto(Project project);
}
