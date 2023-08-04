package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subproject.SubProjectCreateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface SubProjectMapper {
    @Mapping(source = "parentProject.id", target = "parentProjectId")
    SubProjectCreateDto toDto(Project project);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project toEntity(SubProjectCreateDto subProjectCreateDto);
}
