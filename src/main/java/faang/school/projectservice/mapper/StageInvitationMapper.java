package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

        @Mapping(target = "stageId", source = "stage.stageId")
        @Mapping(target = "authorId", source = "author.id")
        @Mapping(target = "invitedId", source = "invited.id")
        StageInvitationDto toDto(StageInvitation stageInvitation);

        StageInvitation toEntity(StageInvitationDto stageDto);

        List<StageInvitationDto> toDto(List<StageInvitation> list);

        List<StageInvitation> toEntity(List<StageInvitationDto> list);
}
