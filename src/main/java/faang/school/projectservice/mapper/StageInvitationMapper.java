package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    StageInvitationDto toDto(StageInvitation stageInvitation);

    List<StageInvitationDto> toDtoList(List<StageInvitation> stageInvitation);

    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

}
