package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    Project toEntity(SubProjectDto createSubProjectDto);

    List<ProjectDto> toDtoList(List<Project> projectList);
}
