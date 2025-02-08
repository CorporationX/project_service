package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(target = "children", ignore = true)
    Project toProjectEntity(CreateSubProjectDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "description")
    SubProjectDto toProjectResponseDto(Project entity);


}