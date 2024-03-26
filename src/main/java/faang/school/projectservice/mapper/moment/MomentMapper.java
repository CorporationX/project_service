package faang.school.projectservice.mapper.moment;

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

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "toProjectsId")
    MomentDto toDto(Moment moment);

    Moment toEntity(MomentDto momentDto);
    @Named("toProjectsId")
    default List<Long> toProjectsId(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}

