package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitationDto toDto(StageInvitation entity);

    StageInvitation toEntity(StageInvitationCreateDto dto);

    List<StageInvitationDto> toInvitationListDto(List<StageInvitation> stageInvitationList);
}