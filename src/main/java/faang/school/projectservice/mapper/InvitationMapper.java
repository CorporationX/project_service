package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvitationMapper {

    @Mapping(target = "stage.stageId", source = "stageId")
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "invited.id", source = "invitedId")
    @Mapping(target = "description", source = "explanation")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto);

    @Mapping(target = "stageId", source = "stage.stageId")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "invitedId", source = "invited.id")
    @Mapping(target = "explanation", source = "description")
    StageInvitationDto toDto(StageInvitation stageInvitation);
}
