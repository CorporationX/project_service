package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project toProject(CreateProjectDto projectDto);

    CreateProjectDto toCreateProjectDto(Project project);

    UpdateProjectDto toUpdateProjectDto(Project project);

    ProjectDto toProjectDto(Project project);

    List<ProjectDto> toProjectDtos(List<Project> projects);
}
