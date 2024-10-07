package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "createdBy.id", target = "teamMemberId")
    ResourceDto toDto(Resource resource);
}
