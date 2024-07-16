package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(target = "stageId", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "invitedId", ignore = true)
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);
}
