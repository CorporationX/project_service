package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubProjectMapper {
    Project toEntity(SubProjectDto subProjectDto);

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    SubProjectDto toDto(Project project);

}
