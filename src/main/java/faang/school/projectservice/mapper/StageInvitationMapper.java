package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Mapper(componentModel = "spring" , injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitationMapper INSTANCE = Mappers.getMapper(StageInvitationMapper.class);
    StageInvitationDto toDto(StageInvitation entity);

    StageInvitation toEntity(StageInvitationDto dto);

    List<StageInvitation> listToEntity(List<StageInvitationDto> stageInvitationDtos);
    List<StageInvitationDto> listToDto(List<StageInvitation> stageInvitations);

    void updateDto(StageInvitationDto stageInvitationDto,
                   @MappingTarget StageInvitation stageInvitation);
}