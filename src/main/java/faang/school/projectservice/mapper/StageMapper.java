package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRoleDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "getExecutorsIds")
    StageDto toDto(Stage stage);

    List<StageDto> toDtos(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "roles", target = "stageRoles")
    Stage toEntity(StageCreateDto stageCreateDto);

    StageRoles toStageRoles(StageRoleDto stageRoleDto);

    List<StageRoles> toStageRoles(List<StageRoleDto> stageRoleDtos);

    @Named("getExecutorsIds")
    default List<Long> getExecutorsIds(List<TeamMember> executors) {
        if (executors == null) {
            return new ArrayList<>();
        }
        return executors.stream()
                .map(TeamMember::getId)
                .toList();
    }
}