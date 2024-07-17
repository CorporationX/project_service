package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
//@Mapper(componentModel = "spring")
public interface MomentMapper {

    @Mapping(source = "projects", target = "projects", qualifiedByName = "projectsToProjectIds")
    MomentDto toDto(Moment moment);

//    @Mapping(target = "resource", ignore = true)
//    @Mapping(target = "imageId", ignore = true)
    @Mapping(source = "projects", target = "projects", qualifiedByName = "projectsIdsToProjects")
    Moment toEntity(MomentDto momentDto);

    @Named("projectsToProjectIds")
    default List<Long> projectsToProjectIds(List<Project> projects){
        if (projects == null) return null;
        return projects.stream()
                .map(Project::getId)
                .toList();
    }

    @Named("projectsIdsToProjects")
    default List<Project> projectsIdsToProjects(List<Long> projectsIds){
        if (projectsIds == null) return null;
        return projectsIds.stream()
                .map(projectId -> {
                    Project project = new Project();
                    project.setId(projectId);
                    return project;
                })
                .toList();
    }
}