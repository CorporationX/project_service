package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipMapperHelper {
    private final TeamMemberRepository teamMemberRepository;

    @Named("mapToInternId")
    public List<Long> mapToInternId(List<TeamMember> interns) {
        return interns.stream()
                .map(TeamMember::getId)
                .toList();
    }

    @Named("mapToInterns")
    public List<TeamMember> mapToInterns(List<Long> internsId) {
        return internsId.stream()
                .map(teamMemberRepository::findById)
                .toList();
    }
}
