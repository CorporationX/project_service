package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentRestDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentRestMapper {
    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjectIds")
    MomentRestDto toDto(Moment moment);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjectIds")
    List<MomentRestDto> toDtoList(List<Moment> momentList);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjects")
    Moment toEntity(MomentRestDto momentDto);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "toProjects")
    List<Moment> toEntityList(List<MomentRestDto> momentDtoList);

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