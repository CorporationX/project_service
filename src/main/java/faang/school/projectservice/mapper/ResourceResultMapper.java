package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceResultDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceResultMapper {
    ResourceResultDto toResultDto(Resource resource);
}
