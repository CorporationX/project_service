package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    List<ProjectDto> toDtos(List<Project> projects);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(target = "viewTime", expression = "java(java.time.LocalDateTime.now())")
    ProjectViewEvent toProjectViewEventDto(Project project, long userId);
}
