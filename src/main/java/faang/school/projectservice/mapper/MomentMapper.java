package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(target = "project.id", ignore = true)
    @Mapping(target = "user.id", ignore = true)
    Moment momentDtoToMoment(MomentDto momentDto);

    MomentDto momentToMomentDto(Moment moment);
}
