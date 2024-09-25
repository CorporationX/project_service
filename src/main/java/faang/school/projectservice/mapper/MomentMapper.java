package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "map")
    MomentDto toDto(Moment moment);

    @Mapping(target = "projects", ignore = true)
    Moment toEntity(MomentDto momentDto);

    @Named("map")
    default List<Long> map(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}
