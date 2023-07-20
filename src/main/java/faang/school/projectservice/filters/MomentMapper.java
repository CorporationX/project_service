package faang.school.projectservice.filters;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.MomentService;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    Moment dtoToMoment(MomentDto momentDto);
    MomentDto momentToDto(Moment moment);
    List<Moment> listDtoToMoment(List<MomentDto> listDto);
    List<MomentDto> listMomentToDto(List<Moment> momentList);
}
