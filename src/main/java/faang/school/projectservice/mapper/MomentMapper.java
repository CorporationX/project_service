package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "userIds", ignore = true)
    Moment toMoment(CreateMomentDto createMomentDto);

    @Mapping(target = "projectIds", expression = "java(mapProjectIds(moment.getProjects()))")
    @Mapping(target = "memberIds", expression = "java(moment.getUserIds() != null ? moment.getUserIds() : java.util.List.of())")
    MomentDto toMomentDto(Moment moment);

    default List<Long> mapProjectIds(List<Project> projects) {
        if (projects == null) {
            return List.of();
        }
        return projects.stream()
                .map(Project::getId)
                .collect(Collectors.toList());
    }
}
