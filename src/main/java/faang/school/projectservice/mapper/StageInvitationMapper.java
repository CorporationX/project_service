package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")

public interface StageInvitationMapper {

    @Mapping(source = "invitedId", target = "invited", qualifiedByName = "idToTeamMember")
    @Mapping(source = "authorId", target = "author", qualifiedByName = "idToTeamMember")
    @Mapping(source = "stageId", target = "stage", qualifiedByName = "idToStage")
    @Mapping(source = "statusId", target = "status", qualifiedByName = "idToStatus")
    StageInvitation toEntity(StageInvitationDto dto);

    @Mapping(source = "invited", target = "invitedId", qualifiedByName = "teamMemberToId")
    @Mapping(source = "author", target = "authorId", qualifiedByName = "teamMemberToId")
    @Mapping(source = "stage", target = "stageId", qualifiedByName = "stageToId")
    @Mapping(source = "status", target = "statusId", qualifiedByName = "statusToId")
    StageInvitationDto toDto(StageInvitation entity);

    @Named("idToTeamMember")
    default TeamMember idToTeamMember(Long id) {
        if (id == null) return null;
        TeamMember member = new TeamMember();
        member.setId(id);
        return member;
    }

    @Named("teamMemberToId")
    default Long teamMemberToId(TeamMember member) {
        return member != null ? member.getId() : null;
    }

    @Named("idToStage")
    default Stage idToStage(Long id) {
        if (id == null) return null;
        Stage stage = new Stage();
        stage.setStageId(id);
        return stage;
    }

    @Named("stageToId")
    default Long stageToId(Stage stage) {
        return stage != null ? stage.getStageId() : null;
    }

    @Named("idToStatus")
    default StageInvitationStatus idToStatus(Long id) {
        if (id == null) return null;
        return StageInvitationStatus.fromId(id);
    }

    @Named("statusToId")
    default Long statusToId(StageInvitationStatus status) {
        return status != null ? status.getId() : null;
    }
}
