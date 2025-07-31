package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    Moment toEntity(MomentDto momentDto);

    MomentDto toDto(Moment moment);

    Moment updateEntityFromDto(MomentDto momentDto, @MappingTarget Moment moment);
}
