package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.redis.InviteSentEventDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
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
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    StageInvitation toModel(StageInvitationDto stageInvitationDto);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    void update(StageInvitationDto stageInvitationDto, @MappingTarget StageInvitation stageInvitation);

    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    List<StageInvitationDto> toDtoList(List<StageInvitation> stageInvitationList);

    @Mapping(source = "stageId", target = "stage.stageId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    List<StageInvitation> toModelList(List<StageInvitationDto> stageInvitationDtoList);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "invitedId", source = "invited.id")
    @Mapping(target = "projectId", source = "stage.project.id")
    InviteSentEventDto toEventDto(StageInvitation stageInvitation);
}
