package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface MomentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "ownerId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Moment toEntity(Project project);
}
