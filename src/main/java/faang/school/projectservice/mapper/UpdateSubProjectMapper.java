package faang.school.projectservice.mapper;

import org.mapstruct.Mapper;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;


@Mapper(componentModel = "spring")
public interface UpdateSubProjectMapper {

    UpdateSubProjectDto toDto(Project subProject);

    Project toEntity(UpdateSubProjectDto dto);
}


