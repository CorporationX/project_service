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
    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjectIds")
    MomentDto toDto(Moment moment);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjectIds")
    List<MomentDto> toDtoList(List<Moment> momentList);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjects")
    Moment toEntity(MomentDto momentDto);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjects")
    List<Moment> toEntityList(List<MomentDto> momentDtoList);

    @Named("toProjectIds")
    default List<Long> toProjectIds(List<Project> projects) {
        return projects.stream()
                .map(Project::getId)
                .toList();
    }

    @Named("toProjects")
    default List<Project> toProjects(List<Long> ids) {
        return ids.stream()
                .map(id -> Project.builder().id(id).build())
                .toList();
    }
}