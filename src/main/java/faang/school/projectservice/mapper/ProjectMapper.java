package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project dtoToProject(ProjectDto projectDto);

    ProjectDto projectToDto(Project project);

    List<ProjectDto> projectsToDtos(List<Project> projects);
}
