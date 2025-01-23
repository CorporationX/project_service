package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(source = "stage.stageId", target = "stageId")
    StageInvitationDto toDto(StageInvitation stageInvitation);


    StageInvitation toEntity(StageInvitationDto stageInvitationDto);


}
