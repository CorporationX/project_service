package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface StageInvitationMapper {

    StageInvitationDto toDto(StageInvitation stageInvitation);

    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

}
