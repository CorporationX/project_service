package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = ProjectService.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {
    Project toEntity(CreateSubProjectDto createSubprojectDto);
}
