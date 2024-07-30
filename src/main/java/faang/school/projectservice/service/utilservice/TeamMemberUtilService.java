package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberUtilService {

    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public TeamMember getByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Team member with user id=%d and project id=%d not found",
                        userId,
                        projectId))
                );
    }

    public void checkExistAllByIds(Collection<Long> teamMemberIds) {
        if (!teamMemberRepository.existAllByIds(teamMemberIds)) {
            throw new NotFoundException(ErrorMessage.SOME_OF_MEMBERS_NOT_EXIST);
        }
    }

    public List<Long> findIdsByProjectIds(Collection<Long> projectIds) {
        return teamMemberRepository.findIdsByProjectIds(projectIds);
    }

    public void checkTeamMembersFitProjects(Collection<Long> teamMemberIds, Collection<Long> projectIds) {
        // Проверяем относятся ли пользователи к проектам, если существует пользователь, который
        // не подходит ни к одному из проектов - бросаем исключение
        // Лишний мембер / недостающий проект

        Set<Long> memberIdsInProjects =
                new HashSet<>(findIdsByProjectIds(projectIds));
        boolean isValid = memberIdsInProjects.containsAll(teamMemberIds);
        if (!isValid) {
            throw new ConflictException(ErrorMessage.MEMBERS_UNFIT_PROJECTS);
        }
    }
}
