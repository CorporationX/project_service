package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "invited.id", target = "inviteeId")
    @Mapping(source = "description", target = "reason")
    StageInvitationDto toDto(StageInvitation stageInvitation);
}