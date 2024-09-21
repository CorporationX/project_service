package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "mapToProjectIds")
    MomentDto toMomentDto(Moment moment);

    List<MomentDto> toMomentDtos(List<Moment> moments);

    @Named("mapToProjectIds")
    default List<Long> mapToProjectIds(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream()
                .map(Project::getId)
                .toList();
    }

    @Mapping(source = "projectIds", target = "projects", qualifiedByName = "mapToProjects")
    Moment toEntity(MomentDto momentDto);

    @Named("mapToProjects")
    default List<Project> mapToProjects(List<Long> projectIds) {
        if (projectIds == null) {
            return null;
        }
        return projectIds.stream()
                .map(projectId -> Project.builder().id(projectId).build())
                .toList();
    }

}
