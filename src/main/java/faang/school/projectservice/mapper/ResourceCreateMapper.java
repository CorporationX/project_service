package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceCreateDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceCreateMapper {

    ResourceCreateDto toResourceCreateDto(Resource resource);

    Resource toResource(ResourceCreateDto resourceCreateDto);
}
