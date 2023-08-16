package faang.school.projectservice.filters.moments;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentDtoUpdate;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.MomentService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    Moment dtoToMoment(MomentDto momentDto);
    MomentDto momentToDto(Moment moment);
    List<Moment> listDtoToMoment(List<MomentDto> listDto);
    List<MomentDto> listMomentToDto(List<Moment> momentList);

    //updatedDto
    Moment dtoUpdatedToMoment(MomentDtoUpdate momentDtoUpdate);
    MomentDtoUpdate momentToDtoUpdated(Moment moment);
    List<Moment> listUpdatedDtoToMoment(List<MomentDtoUpdate> listDto);
    List<MomentDtoUpdate> listMomentToUpdatedDto(List<Moment> momentList);
    Moment updateMomentFromUpdatedDto(MomentDtoUpdate momentDtoUpdate, @MappingTarget Moment deprecatedMoment);

}
