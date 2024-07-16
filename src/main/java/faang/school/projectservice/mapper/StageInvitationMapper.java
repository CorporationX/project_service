package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {

    @Mapping(source = "stage.Id", target = "stageIdPattern")
    @Mapping(source = "author.id", target = "authorIdPattern")
    @Mapping(source = "invited.id", target = "invitedIdPattern")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(target = "stageIdPattern", ignore = true)
    @Mapping(target = "authorIdPattern", ignore = true)
    @Mapping(target = "invitedIdPattern", ignore = true)
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);
}
