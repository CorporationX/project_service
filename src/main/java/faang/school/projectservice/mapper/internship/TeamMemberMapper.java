package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TeamMemberMapper {
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    List<TeamMember> internsToEntity(List<Long> internsId) {
        return internsId.stream()
                .map(teamMemberJpaRepository::findById)
                .flatMap(optional->optional.map(Stream::of).orElseGet(Stream::empty))
                .toList();
    }
}
