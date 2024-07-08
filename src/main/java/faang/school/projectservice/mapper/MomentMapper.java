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
    @Mapping(source = "projects", target = "projectsIDs", qualifiedByName = "projectMapToId")
    MomentDto toDto(Moment moment);

    Moment toEntity(MomentDto userDto);

    List<MomentDto> toDto(List<Moment> users);

    @Named("projectMapToId")
    default List<Long> projectMapToId(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}
