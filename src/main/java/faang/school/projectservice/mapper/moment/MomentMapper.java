package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = ProjectMapper.class, injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    MomentMapper INSTANCE = Mappers.getMapper(MomentMapper.class);

    @Mapping(source = "projects", target = "projects")
    MomentDto toDto(Moment moment);

    @Mapping(source = "projects", target = "projects")
    Moment toEntity(MomentDto momentDto);
}
