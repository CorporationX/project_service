package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MomentMapper {
    Moment toMoment(CreateMomentDto momentDto);

    void update(UpdateMomentDto momentDto, @MappingTarget Moment entity);

    MomentDto toMomentDto(Moment moment);

    List<MomentDto> toListMomentDto(List<Moment> moments);
}
