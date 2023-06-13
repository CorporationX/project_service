package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stage", source = "stageId", qualifiedByName = "mapStage")
    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapTeamMember")
    @Mapping(target = "invited", source = "invitedId", qualifiedByName = "mapTeamMember")
    StageInvitation toEntity(StageInvitationDto dto);

    @Mapping(target = "stageId", source = "stage.stageId")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "invitedId", source = "invited.id")
    StageInvitationDto toDto(StageInvitation entity);

    @Named("mapStage")
    default Stage mapStage(Long stageId) {
        return Stage.builder().stageId(stageId).build();
    }

    @Named("mapTeamMember")
    default TeamMember mapAuthor(Long teamMember) {
        return TeamMember.builder().id(teamMember).build();
    }
}
