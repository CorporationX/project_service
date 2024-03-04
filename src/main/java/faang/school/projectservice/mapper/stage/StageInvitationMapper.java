package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
uses = StageMapper.class,
unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "invited", ignore = true)
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Mapping(target = "stageId", source = "stage.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "invitedId", source = "invited.id")
    StageInvitationDto toDto(StageInvitation stageInvitation);
}
