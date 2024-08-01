package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamMemberUtilService {

    private final TeamMemberRepository teamMemberRepository;

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
