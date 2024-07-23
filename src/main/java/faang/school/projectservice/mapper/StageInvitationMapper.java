package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "author.userId", target = "authorId")
    @Mapping(source = "invited.userId", target = "invitedId")
    @Mapping(source = "stage.stageId", target = "stageId")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "authorId", target = "author.userId")
    @Mapping(source = "invitedId", target = "invited.userId")
    @Mapping(source = "stageId", target = "stage.stageId")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    List<StageInvitationDto> toListDto(List<StageInvitation> stageInvitation);
}
