package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = "spring",
        uses = {ResourceIdMapper.class, ProjectIdMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MomentMapper {

    @Mapping(source = "resource", qualifiedByName = "toListResourceId", target = "resourcesId")
    @Mapping(source = "projects", qualifiedByName = "toListProjectId", target = "projectsId")
    MomentDto toDto(Moment moment);

    @Mapping(source = "resourcesId", qualifiedByName = "toListResource", target = "resource")
    @Mapping(source = "projectsId", qualifiedByName = "toListProject", target = "projects")
    Moment toEntity(MomentDto momentDto);
}
