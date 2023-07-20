package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    //@Mapping(source = "stages", target = "stageIds", qualifiedByName = "map")
    StageInvitationDto toDto(StageInvitation entity);

    //@Mapping(target = "users", ignore = true)
    StageInvitation toEntity(StageInvitationDto dto);

}
