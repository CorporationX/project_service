package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface StageInvitationMapper {

    StageInvitationDto toDto(StageInvitation stageInvitation);

    List<StageInvitationDto> toDto(List<StageInvitation> stageInvitation);

    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

}
