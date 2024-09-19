package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationDtoMapper {

    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    StageInvitationDto toDto(StageInvitation stageInvitation);

    List<StageInvitationDto> toDtos(List<StageInvitation> stageInvitations);
}
