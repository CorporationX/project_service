package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(target = "stageId", source = "stage.stageId")
    @Mapping(target = "invitedId", source = "invited.userId")
    StageInvitationDto toDto(StageInvitation entity);

    StageInvitation toEntity(StageInvitationCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    List<StageInvitationDto> toInvitationListDto(List<StageInvitation> stageInvitationList);
}

