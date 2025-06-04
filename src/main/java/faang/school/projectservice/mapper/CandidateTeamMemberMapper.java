package faang.school.projectservice.mapper;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        imports = {
                java.util.Collections.class,
                java.util.List.class
        }
)
public interface CandidateTeamMemberMapper {

    @Mapping(target = "nickname", source = "username")
    @Mapping(
            target = "roles",
            expression = "java(candidate.getVacancy().getPosition() == null ? " +
                    "Collections.emptyList() : List.of(candidate.getVacancy().getPosition()))")
    TeamMember candidateToTeamMember(Candidate candidate);
}
