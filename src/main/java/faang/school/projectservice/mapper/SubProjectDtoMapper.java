package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "String", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectDtoMapper {

    CreateSubProjectDto toDTO(Project project);

}