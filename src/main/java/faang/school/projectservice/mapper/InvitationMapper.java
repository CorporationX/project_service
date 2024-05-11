package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring")
public interface InvitationMapper {
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);
    StageInvitationDto toDto(StageInvitation stageInvitation);
}
