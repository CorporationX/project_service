package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "parentProjectId", expression =
            "java(project.getParentProject() != null ? project.getParentProject().getId() : null)")
    ProjectDto toDto(Project project);
    Project toEntity(ProjectDto projectDto);
}
