package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitationDto toInvitationDto(StageInvitation invitation);
    StageInvitation toInvitation(StageInvitationDto StageInvitationDto);

    List<StageInvitationDto> toDtoList(List<StageInvitation> invitations);
    List<StageInvitation> toInvitationList(List<StageInvitationDto> invitationDtos);
}
