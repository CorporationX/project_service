package faang.school.projectservice.mapper.resource;


import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;


public interface ResourceMapper {
    ResourceDto toDto(Resource resource);

    Resource toEntity(ResourceDto resourceDto);

}
