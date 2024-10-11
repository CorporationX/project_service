package faang.school.projectservice.model.mapper.subproject;

import faang.school.projectservice.model.dto.project.ProjectDto;
import faang.school.projectservice.model.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    Project toEntity(SubProjectDto createSubProjectDto);

    List<ProjectDto> toDtoList(List<Project> projectList);
}
