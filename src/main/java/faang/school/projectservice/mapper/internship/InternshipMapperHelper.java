package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipMapperHelper {
    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> mapToInters(List<Long> internsId) {
        return internsId.stream()
                .map(teamMemberRepository::findById)
                .toList();
    }

    public List<Long> mapToInternsId(List<TeamMember> interns) {
        return interns.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
