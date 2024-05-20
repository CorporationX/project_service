package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    Moment dtoToMoment(MomentDto momentDto);

    // update entity from dto https://www.baeldung.com/spring-data-partial-update,
    void updateMomentFromDto(MomentDto momentDto, @MappingTarget Moment moment);

    MomentDto momentToDto(Moment moment);

    List<MomentDto> momentsToDtoList(List<Moment> all);
}

