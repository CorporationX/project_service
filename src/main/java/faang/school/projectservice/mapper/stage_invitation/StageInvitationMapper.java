package faang.school.projectservice.mapper.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.publisher.event.InviteSentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    StageInvitationDto toDTO(StageInvitation stageInvitation);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    List<StageInvitationDto> toDTOList(List<StageInvitation> stageInvitationList);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    List<StageInvitation> toEntityList(List<StageInvitationDto> stageInvitationDtoList);

    InviteSentEvent toEvent(StageInvitationDto stageInvitationDto);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    void update(StageInvitationDto stageInvitationDto, @MappingTarget StageInvitation stageInvitation);
}
