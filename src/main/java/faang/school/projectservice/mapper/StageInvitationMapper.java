package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "status", ignore = true)
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    List<StageInvitationDto> toDto(List<StageInvitation> stageInvitations);
}
