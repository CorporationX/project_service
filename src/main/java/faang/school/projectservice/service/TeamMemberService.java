package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberJpaRepository teamMemberRepository;

    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("InitiativeService.update: event with id {} not found", id);
                    return  new EntityNotFoundException(String.format("TeamMember with id %s not found", id));
                });
    }
}
