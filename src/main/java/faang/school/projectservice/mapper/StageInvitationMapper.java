package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toStageInvitationDto(StageInvitation entity);

    @Mapping(target = "stage.stageId", source = "stageId")
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "invited.id", source = "invitedId")
    StageInvitation toStageInvitation(StageInvitationDto dto);
}