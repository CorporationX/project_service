package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toEntity(ProjectCoverDto projectDto);

    ProjectCoverDto toProjectCoverDto(Project project);

    Project toEntity(ProjectDto projectDto);

    ProjectDto toProjectDto(Project project);
}
