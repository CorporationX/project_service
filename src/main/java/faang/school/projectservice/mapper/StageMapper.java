package faang.school.projectservice.mapper;

import faang.school.projectservice.model.dto.StageDto;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "executors", target = "teamMemberIds", qualifiedByName = "mapExecutor")
    StageDto toDto(Stage stage);

    List<StageDto> toDtoList(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "stageRoles", ignore = true)
    Stage toEntity(StageDto stageDto);

    @Named("mapExecutor")
    default List<Long> mapExecutor(List<TeamMember> teamMembers) {
        return teamMembers.stream().map(TeamMember::getId).toList();
    }
}
