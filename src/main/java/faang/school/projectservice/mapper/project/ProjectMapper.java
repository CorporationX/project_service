package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import java.util.List;

import faang.school.projectservice.model.ProjectStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);
    Project toProject(ProjectDto projectDto);

    default Project toProject(ProjectDto projectDto, ProjectStatus projectStatus, String description) {
        Project project = toProject(projectDto);
        project.setStatus(projectStatus);
        project.setDescription(description);
        return project;
    }

    List<ProjectDto> toDto(List<Project> projects);
}
