package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    StageRoles toEntity(StageRolesDto stageRolesDto);

    StageRolesDto toDto(StageRoles stageRoles);
}
