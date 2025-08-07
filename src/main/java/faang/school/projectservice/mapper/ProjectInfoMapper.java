package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectInfoMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Mapping(target = "parentProjectName", source = "project.parentProject.name")
    @Mapping(target = "childrenProjectNames", source = "project.children", qualifiedByName = "mapChildrenNames")
    @Mapping(target = "ownerUsername", source = "userName")
    @Mapping(target = "createdAt", source = "project.createdAt", qualifiedByName = "formatDate")
    ProjectInfoDto toProjectInfoDto(Project project, String userName);

    @Named("mapChildrenNames")
    default List<String> mapChildrenNames(List<Project> children) {
        return children == null ? List.of() :
                children.stream()
                        .map(Project::getName)
                        .toList();
    }

    @Named("formatDate")
    default String formatDate(LocalDateTime dateTime) {
        return (dateTime == null) ? null : FORMATTER.format(dateTime);
    }
}