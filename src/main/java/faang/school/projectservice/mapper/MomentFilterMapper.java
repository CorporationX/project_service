package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentFilterMapper {
    MomentDto toMomentDto(MomentFilterDto momentFilterDto);
}
