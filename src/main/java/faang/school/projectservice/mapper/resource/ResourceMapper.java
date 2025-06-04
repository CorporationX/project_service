package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "project.id", target = "projectId")
    ResponseResourceDto toDto(Resource resource);

    Resource toEntity(ResponseResourceDto dto);
}
