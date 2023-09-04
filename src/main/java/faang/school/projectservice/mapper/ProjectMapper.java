package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "parentProject", source = "parentProjectId")
    Project toProject(ProjectDto projectDto);

    default Project map(Long parentProjectId) {
        if (parentProjectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(parentProjectId);
        return project;
    }

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    ProjectDto toProjectDto(Project project);

    List<ProjectDto> toListProjectDto(List<Project> projects);
}
