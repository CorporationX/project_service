package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectDtoMapper {

    CreateSubProjectDto toDTO(Project project);

}