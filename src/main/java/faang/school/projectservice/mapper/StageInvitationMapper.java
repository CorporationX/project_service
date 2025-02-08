package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.AcceptInvitationRequest;
import faang.school.projectservice.dto.invitation.AcceptInvitationResponse;
import faang.school.projectservice.dto.invitation.DeclineInvitationRequest;
import faang.school.projectservice.dto.invitation.DeclineInvitationResponse;
import faang.school.projectservice.dto.invitation.InvitationDto;
import faang.school.projectservice.dto.invitation.SendInvitationRequest;
import faang.school.projectservice.dto.invitation.SendInvitationResponse;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(target = "stage.stageId", source = "stageId")
    @Mapping(target = "author.id", source = "author")
    @Mapping(target = "invited.id", source = "invited")
    @Mapping(target = "status", constant = "PENDING")
    StageInvitation toStageInvitation(SendInvitationRequest request);

    @Mapping(target = "stageId", source = "stage.stageId")
    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "invited", source = "invited.id")
    @Mapping(target = "status", expression = "java(stageInvitation.getStatus().name())")
    SendInvitationResponse toSendInvitationResponse(StageInvitation stageInvitation);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", constant = "ACCEPTED")
    StageInvitation toAcceptedStageInvitation(AcceptInvitationRequest request);

    @Mapping(target = "status", expression = "java(stageInvitation.getStatus().name())")
    AcceptInvitationResponse toAcceptInvitationResponse(StageInvitation stageInvitation);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", constant = "REJECTED")
    @Mapping(target = "declineReason", source = "description")
    StageInvitation toDeclinedStageInvitation(DeclineInvitationRequest request);

    @Mapping(target = "status", expression = "java(stageInvitation.getStatus().name())")
    @Mapping(target = "description", source = "declineReason")
    DeclineInvitationResponse toDeclineInvitationResponse(StageInvitation stageInvitation);

    @Mapping(target = "stageId", source = "stage.stageId")
    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "invited", source = "invited.id")
    @Mapping(target = "status", expression = "java(stageInvitation.getStatus().name())")
    InvitationDto toInvitationDto(StageInvitation stageInvitation);
}
