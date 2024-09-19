package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toStageInvitationDto(StageInvitation entity);

    @Mapping(target = "stage", source = "stageId", qualifiedByName = "stageFromId")
    @Mapping(target = "author", source = "authorId", qualifiedByName = "teamMemberFromId")
    @Mapping(target = "invited", source = "invitedId", qualifiedByName = "teamMemberFromId")
    StageInvitation toStageInvitation(StageInvitationDto dto);

    @Named("teamMemberFromId")
    default TeamMember teamMemberFromId(Long id) {
        if (id == null) return null;
        TeamMember teamMember = new TeamMember();
        teamMember.setId(id);
        return teamMember;
    }

    @Named("stageFromId")
    default Stage stageFromId(Long id) {
        if (id == null) return null;
        Stage stage = new Stage();
        stage.setStageId(id);
        return stage;
    }
}