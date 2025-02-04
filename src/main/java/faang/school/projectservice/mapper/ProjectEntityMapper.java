package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import io.micrometer.common.util.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectEntityMapper {

    Project toEntity(ProjectCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", conditionQualifiedByName = "isNotBlank")
    void updateEntityFromDto(ProjectUpdateDto dto, @MappingTarget Project entity);

    ProjectReadDto toProjectDto(Project project);

    @Condition
    @Named("isNotBlank")
    default boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }
}
