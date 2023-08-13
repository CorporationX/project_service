package faang.school.projectservice.filters.mappers;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitationDto entityToDto(StageInvitation stageInvitation);
    StageInvitation dtoToEntity(StageInvitationDto stageInvitationDto);
    List<StageInvitation> listDtoToEntity(List<StageInvitationDto> stageInvitationDtos);
    List<StageInvitationDto> listEntityToDto(List<StageInvitation> stageInvitations);
    void updateEntityViaDto(StageInvitationDto stageInvitationDto, @MappingTarget StageInvitation stageInvitation);
}

