package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProjectMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    MomentMapper INSTANCE = Mappers.getMapper(MomentMapper.class);

    MomentDto toDto(Moment moment);

    Moment toEntity(MomentDto momentDto);

    List<MomentDto> toListDto(List<Moment> moments);
}
