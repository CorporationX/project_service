package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(target = "projectId", source = "project.id")
    ResourceDto toDto(Resource resource);

    @Mapping(target = "project.id", source = "projectId")
    Resource toEntity(ResourceDto resourceDto);

    @Mapping(target = "project.id", source = "projectId")
    void update(ResourceDto resourceDto,@MappingTarget Resource resource);
}
