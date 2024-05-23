package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "invitedId", source = "invited.id")
    StageInvitationDto toDto(StageInvitation stageInvitation);
}