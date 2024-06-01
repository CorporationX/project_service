package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {ProjectStatus.class, ProjectVisibility.class},
        uses = {ProjectMapperUtil.class}
)
public interface ProjectMapper {

    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "visibility", defaultValue = "PRIVATE")
    @Mapping(target = "ownerId", qualifiedByName = {"UserMapperUtil", "setOwner"}, source = "ownerId")
    Project createDtoToProject(ProjectCreateDto projectCreateDto);

    Project dtoToProject(ProjectDto projectDto);

    ProjectDto projectToDto(Project project);

    List<ProjectDto> projectsToDtos(List<Project> projects);
}
