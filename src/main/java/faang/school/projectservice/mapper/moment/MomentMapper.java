package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    MomentDto toDto(Moment moment);

    List<MomentDto> toListDto(List<Moment> momentList);

    Moment toEntity(MomentDto momentDto);

    List<Moment> toEntityList(List<MomentDto> momentDtoList);
}