package faang.school.projectservice.mapper.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDtoResponse;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDtoRequest;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationDtoMapper {

    StageInvitation toEntity(StageInvitationDtoResponse stageInvitationDtoResponse);

    StageInvitation toEntity(StageInvitationDtoRequest stageInvitationDto);

    StageInvitationDtoResponse toDto(StageInvitation stageInvitation);

    List<StageInvitationDtoRequest> toDtos(List<StageInvitation> stageInvitations);
}
