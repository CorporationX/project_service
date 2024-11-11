package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    StageInvitationDto toDto(StageInvitation stageInvitation);

    StageInvitation toEntity(StageInvitationDto stageInvitationDto);
}
