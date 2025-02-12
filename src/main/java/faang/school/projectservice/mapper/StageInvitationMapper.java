package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(source = "stage.stageId", target = "stageId")
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "authorId", target = "author", qualifiedByName = "teamMemberId to teamMember")
    @Mapping(source = "invitedId", target = "invited", qualifiedByName = "teamMemberId to teamMember")
    @Mapping(source = "stageId", target = "stage", qualifiedByName = "stageId to stage entity")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto,
                             @Context TeamMemberRepository teamMemberRepository,
                             @Context StageRepository stageRepository);

    @Named("teamMemberId to teamMember")
    default TeamMember teamMemberIdToTeamMember(Long id, @Context TeamMemberRepository teamMemberRepository) {
        return teamMemberRepository.getReferenceById(id);
    }

    @Named("stageId to stage entity")
    default Stage stageIdToStage(Long stageId, @Context StageRepository stageRepository) {
        return stageRepository.getReferenceById(stageId);
    }
}
