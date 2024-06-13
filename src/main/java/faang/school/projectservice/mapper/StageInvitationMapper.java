package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDto(StageInvitation invitation);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    StageInvitation toEntity(StageInvitationDto invitationDto);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    StageInvitation toEntity(StageInvitationCreateDto invitationDto);
}
