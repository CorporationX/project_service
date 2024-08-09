package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "fromProjectsToProjectIds")
    MomentDto toDto(Moment moment);

    Moment update(@MappingTarget Moment moment, MomentDto momentDto);

    @Named("fromProjectsToProjectIds")
    default List<Long> toProjectIds(List<Project> projects) {
        if (projects == null) {
            return null;
        } else {
            return projects.stream().map(Project::getId).collect(Collectors.toList());
        }
    }
}
