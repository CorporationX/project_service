package faang.school.projectservice.model.mapper.moment;

import faang.school.projectservice.model.dto.moment.MomentDto;
import faang.school.projectservice.model.entity.Moment;
import faang.school.projectservice.model.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "mapProjectsToIds")
    MomentDto toMomentDto(Moment moment);

    Moment toMoment(MomentDto momentDto);

    List<MomentDto> toMomentDto(List<Moment> moments);

    @Named("mapProjectsToIds")
    default List<Long> mapProjectsToIds(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}
