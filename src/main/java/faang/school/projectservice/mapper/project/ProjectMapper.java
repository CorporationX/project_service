package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "children", target = "children", qualifiedByName = "mapToLongChildrenId")
    ProjectDto toDto(Project project);

    Project toEntity(CreateSubProjectDto createSubProjectDto);

    @Named("mapToLongChildrenId")
    default List<Long> mapToLongChildrenId(List<Project> children) {
        if (children != null) {
            return children.stream()
                    .map(Project::getId)
                    .toList();
        }
        return Collections.emptyList();
    }
}