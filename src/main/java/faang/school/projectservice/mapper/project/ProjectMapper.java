package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    Project toProject(ProjectDto project);

    List<ProjectDto> toDtos(List<Project> projects);

    @BeanMapping(ignoreByDefault = true,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "status", target = "status")
    @Mapping(source = "description", target = "description")
    void updateProject(ProjectDto projectDto, @MappingTarget Project project);

}
