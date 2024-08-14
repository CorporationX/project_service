package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MomentMapperHelper.class})
public interface MomentMapper {

    @Mapping(source = "projectIds", target = "projects")
    Moment toEntity(MomentDto momentDto);
}
