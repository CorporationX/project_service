package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    StageInvitationDto toDto(StageInvitation stageInvitation);

    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    List<StageInvitationDto> toDto(List<StageInvitation> stageInvitations);
}
