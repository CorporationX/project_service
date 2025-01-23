package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StageInvitationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(source = "stage.stageId", target = "stageId")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "authorId", target = "author", qualifiedByName = "teamMemberId to teamMember")
    @Mapping(source = "invitedId", target = "invited", qualifiedByName = "teamMemberId to teamMember")
    @Mapping(source = "stageId", target = "stage", qualifiedByName = "stageId to stage entity")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);


}
