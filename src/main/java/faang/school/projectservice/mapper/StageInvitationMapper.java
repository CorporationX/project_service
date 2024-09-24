package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {
    StageInvitationDto toDto(StageInvitation invitation);
    StageInvitation toEntity(StageInvitationDto dto);
}