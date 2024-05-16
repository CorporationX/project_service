package faang.school.projectservice.mapper;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;

import faang.school.projectservice.model.TeamRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Arrays;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        imports = {TeamRole.class, Arrays.class}
)
public interface CandidateTeamMemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(source = "candidate.userId", target = "userId")
    @Mapping(target = "roles", expression = "java(Arrays.asList(TeamRole.INTERN))")
    TeamMember candidateToTeamMember(Candidate candidate, Team team);
}
