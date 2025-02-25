package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMember findById(long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с ID %d не найден", teamMemberId)
                ));
    }

    public TeamMember findById(@NotNull Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с таким id: %d не найден", teamMemberId))
                );
    }

    public void areTeamMembersExist(List<Long> userIds) {
        userIds.forEach(id -> {
            if (getTeamMembersByUserId(id).isEmpty()) {
                throw new EntityNotFoundException("Участника команды с userId=" + id + " не существует");
            }
        });
    }

    private List<TeamMember> getTeamMembersByUserId(Long userId) {
        return teamMemberRepository.findByUserId(userId);
    }
}
