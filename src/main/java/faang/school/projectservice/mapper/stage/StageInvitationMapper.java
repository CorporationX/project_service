package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
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
    StageInvitationDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "authorId", target = "author", qualifiedByName = "teamMemberId to teamMember")
    @Mapping(source = "invitedId", target = "invited", qualifiedByName = "teamMemberId to teamMember")
    StageInvitation toEntity(StageInvitationDto stageInvitationDto, @Context TeamMemberRepository teamMemberRepository);

    @Named("teamMemberId to teamMember")
    default TeamMember teamMemberIdToTeamMember(Long id, @Context TeamMemberRepository teamMemberRepository) {
        return teamMemberRepository.findById(id);
    }
}
