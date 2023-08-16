package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_roles.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.FIELD)
public interface StageRolesMapper {

    StageRolesDto toDto(StageRoles stageRoles);

    StageRoles toEntity(StageRolesDto stageRolesDto);
}