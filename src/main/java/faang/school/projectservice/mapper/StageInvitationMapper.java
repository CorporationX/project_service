package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.DtoStageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitationMapper INSTANCE = Mappers.getMapper(StageInvitationMapper.class);

    @Mapping(source = "userIdAuthor", target = "author.userId")
    @Mapping(source = "userIdInvited", target = "invited.userId")
    StageInvitation toStageInvitation(DtoStageInvitation dto);

    DtoStageInvitation toDto(StageInvitation stageInvitation);
}
