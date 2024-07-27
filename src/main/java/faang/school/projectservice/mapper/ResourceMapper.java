package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceResponseDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "createdBy.id", target = "creatorId")
    @Mapping(source = "updatedBy.id", target = "updaterId")
    @Mapping(source = "project.id", target = "projectId")
    ResourceResponseDto toResponseDto(Resource resource);
}
