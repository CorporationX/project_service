package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceCreationDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.UpdateResourceDto;
import faang.school.projectservice.model.resource.Resource;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "toZonedDateTime")
    ResourceDto toDto(Resource resource);

    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "toZonedDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "toZonedDateTime")
    UpdateResourceDto toUpdateDto(Resource resource);

    @Mapping(target = "name", expression = "java(resourceCreationDto.getMultipartFile().getOriginalFilename())")
    @Mapping(target = "key", source = "fileKey")
    @Mapping(target = "size", expression = "java( java.math.BigInteger.valueOf(resourceCreationDto.getMultipartFile().getSize()) )")
    @Mapping(target = "type", expression = "java( faang.school.projectservice.model.resource.ResourceType.getResourceType(resourceCreationDto.getMultipartFile().getContentType()) )")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdBy", source = "teamMember")
    @Mapping(target = "project", source = "project")
    Resource toCreateEntity(ResourceCreationDto resourceCreationDto);

    @Named(value = "toZonedDateTime")
    default ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null){
            return null;
        }

        return localDateTime.atZone(ZoneId.systemDefault());
    }
}
