package faang.school.projectservice.validation.team_member;

import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberValidatorImpl implements TeamMemberValidator {

    private final TeamMemberRepository teamMemberRepository;

    @Override
    public void validateExistence(long id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new NotFoundException("TeamMember with id=" + id + " does not exist");
        }
    }
}
