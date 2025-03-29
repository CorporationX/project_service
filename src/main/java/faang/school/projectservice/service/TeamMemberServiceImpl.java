package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public Optional<TeamMember> getTeamMemberById(long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId);
    }

    public TeamMember validateAndGetAuthor(OpenVacancyRequestDto requestDto) {
        var author = getTeamMemberById(requestDto.authorId())
                .orElseThrow(() -> new DataValidationException(
                        "Author with id %d is not found".formatted(requestDto.authorId())));

        var isRightRole = author.getRoles()
                .stream()
                .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);
        if (!isRightRole) {
            throw new DataValidationException(
                    "Current author roles are %s. Only OWNER and MANAGER is possible".formatted(
                            String.join(",", author.getRoles().stream().map(Enum::toString).toList())));
        }

        return author;
    }
}
