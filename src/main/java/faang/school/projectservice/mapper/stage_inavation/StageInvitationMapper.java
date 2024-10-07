package faang.school.projectservice.mapper.stage_inavation;


import faang.school.projectservice.dto.stage_inavation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDto(StageInvitation invitation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(target = "status", ignore = true)
    StageInvitation toEntity(StageInvitationDto invitationDto);
}