package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    @Mapping(source = "stage.id", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "stageId", target = "stage.id")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    StageInvitation toModel(StageInvitationDto stageInvitationDto);
}
