package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(target = "children", ignore = true)
    Project toProjectEntity(CreateSubProjectDto dto);

    SubProjectResponseDto toSubProjectResponseDto(Project entity);

    List<SubProjectResponseDto> toSubProjectResponseDtos(List<Project> entities);

}

