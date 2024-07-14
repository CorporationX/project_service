package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    @Mapping(target = "stage", ignore = true)
    List<StageRoles> toEntities(List<StageRolesDto> dtos);


}
