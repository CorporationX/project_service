package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {


    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDTO(Resource resource);

    @Mapping(source = "createdById", target = "createdBy.id")
    @Mapping(source = "updatedById", target = "updatedBy.id")
    @Mapping(source = "projectId", target = "project.id")
    Resource toEntity(ResourceDto dto);

    List<ResourceDto> toDTOList(List<Resource> resources);

    List<Resource> toEntityList(List<ResourceDto> dtos);
}
