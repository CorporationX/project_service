package faang.school.projectservice.mapper.project.stage;

import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageRoleDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(source = "projectId", target = "project.id")
    Stage toStage(StageDto stageDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "getExecutorsIds")
    StageDto toStageDto(Stage stage);

    List<StageDto> toStageDtos(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "roles", target = "stageRoles")
    Stage toStage(StageCreateDto stageCreateDto);

    @Mapping(source = "stageId", target = "stage.stageId")
    StageRoles toStageRoles(StageRoleDto stageRoleDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    StageRoleDto toStageRoleDto(StageRoles stageRoles);

    List<StageRoles> toStageRoles(List<StageRoleDto> stageRoleDtos);

    @Named("getExecutorsIds")
    default List<Long> getExecutorsIds(List<TeamMember> executors) {
        // как лучше обработать null здесь?
        if (executors == null) {
            return null;
        }
        return executors.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
