package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateProjectMapper {
    UpdateProjectMapper INSTANCE = Mappers.getMapper(UpdateProjectMapper.class);

    UpdateProjectDto toDto(Project entity);
}
