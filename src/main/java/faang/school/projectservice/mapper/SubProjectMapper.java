package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubProjectMapper {
    Project toSubProject(CreateSubProjectDto createSubProjectDto);

    SubProjectDto toSubProjectDto(Project subProject);

    List<SubProjectDto> toSubProjectList(List<Project> projects);

    void update(UpdateSubProjectDto updateSubProjectDto, @MappingTarget Project subProject);
}
