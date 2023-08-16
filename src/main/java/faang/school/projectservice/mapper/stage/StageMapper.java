package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface StageMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "stageRoleIds", source = "stageRoles", qualifiedByName = "mapStageRoles")
    @Mapping(target = "teamMemberIds", source = "executors", qualifiedByName = "mapTeamMembers")
    StageDto toDto(Stage stage);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "stageRoles", ignore = true)
    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Stage toEntity(StageDto dto);

    List<StageDto> toDtoList(List<Stage> stages);

    @Named("mapStageRoles")
    static List<Long> mapStageRoles(List<StageRoles> stageRoles) {
        return stageRoles.stream()
                .map(StageRoles::getId)
                .toList();
    }

    @Named("mapTeamMembers")
    static List<Long> mapTeamMembers(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
