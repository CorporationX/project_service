package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {
    @Mapping(target = "parentProject", ignore = true)
    Project toEntity(SubProjectDto subProjectDto);

    SubProjectDto toDto(Project project);
}
