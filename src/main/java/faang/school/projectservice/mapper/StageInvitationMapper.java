package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    @Mapping(source = "status", target = "status")
    StageInvitationDto toDto(StageInvitation stageInvitation);
    @Mapping(source = "status", target = "status")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    List<StageInvitationDto> toDtoList(List<StageInvitation> invitations);
}
