package faang.school.projectservice.mapper.projectMapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toEntityFromCreateDto(ProjectCreateDto projectCreateDto);

    ProjectResponseDto toResponseDtoFromEntity(Project project);
}

