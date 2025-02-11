package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    ResourceResponseDto toResourceResponseDto(Resource resource);
}
