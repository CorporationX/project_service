package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "toProjectIds")
    MomentDto toDto(Moment moment);

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "toProjectIds")
    List<MomentDto> toDtoList(List<Moment> momentList);

    @Mapping(source = "projectIds", target = "projects", qualifiedByName = "toProjects")
    Moment toEntity(MomentDto momentDto);

    @Mapping(source = "projectIds", target = "projects", qualifiedByName = "toProjects")
    List<Moment> toEntityList(List<MomentDto> momentDtoList);

    //Костыль для projects
    @Named("toProjectIds")
    default List<Long> toProjectIds(List<Project> projects) {
            return projects.stream()
                    .filter(Objects::nonNull)
                    .map(Project::getId)
                    .toList();
    }

    @Named("toProjects")
    default List<Project> toProjects(List<Long> ids) {
            return ids.stream()
                    .filter(Objects::nonNull)
                    .map(id -> Project.builder().id(id).build())
                    .toList();
    }
}