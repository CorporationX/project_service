package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageCreateRequestDto;
import faang.school.projectservice.dto.stage.StageCreateResponseDto;
import faang.school.projectservice.dto.stage.StageResponseDto;
import faang.school.projectservice.dto.stage.StageUpdateRequestDto;
import faang.school.projectservice.dto.stage.StageUpdateResponseDto;
import faang.school.projectservice.dto.stage.stage_role.StageRolesUpdateRequestDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = StageRolesMapper.class)
public interface StageMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "stageRolesDtos", source = "stageRoles")
    StageCreateResponseDto toCreateResponseDto(Stage savedStage);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "stageRolesDtos", source = "stageRoles")
    @Mapping(target = "teamMemberIds", source = "executors", qualifiedByName = "mapTeamMembersToIds")
    StageUpdateResponseDto toUpdateResponseDto(Stage savedStage);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "stageRolesDtos", source = "stageRoles")
    StageResponseDto toResponseDto(Stage savedStage);

    @Mapping(target = "stageRoles", source = "stageRolesDtos")
    Stage toStage(StageCreateRequestDto stageCreateRequestDto);

    void updateStage(StageUpdateRequestDto stageUpdateRequestDto, @MappingTarget Stage stage);

    default void updateStageRoles(List<StageRolesUpdateRequestDto> dtos,
                                  List<StageRoles> entities) {
        if (dtos == null || entities == null) {
            return;
        }

        StageRolesMapper stageRolesMapper = Mappers.getMapper(StageRolesMapper.class);

        Map<Long, StageRolesUpdateRequestDto> dtoMap = dtos.stream()
                .collect(Collectors.toMap(StageRolesUpdateRequestDto::getId, Function.identity()));

        for (StageRoles entity : entities) {
            StageRolesUpdateRequestDto dto = dtoMap.get(entity.getId());
            if (dto != null) {
                stageRolesMapper.update(dto, entity);
            }
        }
    }

    @Named("mapTeamMembersToIds")
    default List<Long> mapTeamMembersToIds(List<TeamMember> teamMembers) {
        if (CollectionUtils.isEmpty(teamMembers)) {
            return null;
        }
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
